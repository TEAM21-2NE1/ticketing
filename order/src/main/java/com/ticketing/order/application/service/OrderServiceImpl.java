package com.ticketing.order.application.service;

import com.ticketing.order.application.dto.client.CancelSeatRequestDto;
import com.ticketing.order.application.dto.client.ConfirmSeatRequestDto;
import com.ticketing.order.application.dto.client.SeatInfoResponseDto;
import com.ticketing.order.application.dto.client.SeatStatus;
import com.ticketing.order.application.dto.request.CreateOrderRequestDto;
import com.ticketing.order.application.dto.response.CreateOrderResponseDto;
import com.ticketing.order.application.dto.response.CreateOrderResponseDto.SeatDetail;
import com.ticketing.order.common.exception.OrderException;
import com.ticketing.order.common.exception.SeatException;
import com.ticketing.order.common.response.ExceptionMessage;
import com.ticketing.order.domain.model.Order;
import com.ticketing.order.domain.model.OrderStatus;
import com.ticketing.order.domain.model.RunningQueue;
import com.ticketing.order.domain.model.User;
import com.ticketing.order.domain.model.WaitingQueue;
import com.ticketing.order.domain.repository.OrderRepository;
import com.ticketing.order.infrastructure.PerformanceClient;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RunningQueue runningQueue;
    private final WaitingQueue waitingQueue;
    private final PerformanceClient performanceClient;
    private final RedisTemplate<String, User> redisTemplate;
    private final SeatOrderService seatOrderService;

    private final ReentrantLock lock = new ReentrantLock();

    // 주문 생성 - 사용자의 상태에 따라 즉시 처리, 대기열 처리, 또는 대기 상태로 전환
    @Transactional
    public CreateOrderResponseDto createOrder(CreateOrderRequestDto requestDto, String userId,
            String userRole, String userEmail) {
        lock.lock();
        try {
            User user = User.of(userId);

            // 공연 정보
            performanceClient.getPerformance(userId, userRole, userEmail,
                    requestDto.performanceId());
            Order order = orderRepository.findBySelectedSeatIds(requestDto.selectedSeatIds());

            if (order != null) {
                return handleExistingOrder(requestDto, userId, user, order);
            }

            validateSeats(requestDto.performanceId(), requestDto.selectedSeatIds(), userId);

            return processOrderByUserStatus(requestDto, userId, user, userRole, userEmail);

        } finally {
            lock.unlock();
        }
    }

    // 기존 주문이 있는 경우 처리
    private CreateOrderResponseDto handleExistingOrder(CreateOrderRequestDto requestDto,
            String userId, User user, Order existingOrder) {
        if (!existingOrder.getUserId().equals(userId)) {
            throw new SeatException(ExceptionMessage.SEAT_NOT_SELECTED_BY_USER);
        }

        if (runningQueue.check(user)) {
            return returnExistingOrderDetails(requestDto, existingOrder);
        } else if (runningQueue.available()) {
            return returnExistingOrderDetails(requestDto, existingOrder);
        } else {
            return processWaitingOrder(user);
        }
    }

    // 기존 주문의 상세 정보 반환
    private CreateOrderResponseDto returnExistingOrderDetails(CreateOrderRequestDto requestDto,
            Order order) {
        List<SeatDetail> seatDetails = createSeatDetails(requestDto.performanceId(),
                requestDto.selectedSeatIds());
        return CreateOrderResponseDto.from(order, seatDetails);
    }

    // 좌석 상태 검증
    private void validateSeats(UUID performanceId, List<UUID> seatIds, String userId) {
        for (UUID seatId : seatIds) {
            SeatInfoResponseDto seatInfo = seatOrderService.getSeatFromRedis(performanceId, seatId);

            if (seatInfo == null) {
                throw new SeatException(ExceptionMessage.SEAT_NOT_FOUND);
            }

            if (seatInfo.getSeatStatus() == SeatStatus.BOOKED) {
                throw new SeatException(ExceptionMessage.SEAT_ALREADY_BOOKED);
            }

            validateSeatHoldStatus(seatInfo, userId);
        }
    }

    // HOLD 상태와 사용자 일치 여부 검증
    private void validateSeatHoldStatus(SeatInfoResponseDto seatInfo, String userId) {
        boolean isStatusMatch = seatInfo.getSeatStatus() == SeatStatus.HOLD;
        boolean isUserMatch = Long.parseLong(userId) == seatInfo.getUserId();

        if (!isStatusMatch || !isUserMatch) {
            throw new SeatException(ExceptionMessage.SEAT_NOT_SELECTED_BY_USER);
        }
    }

    // 사용자 상태에 따른 주문 처리
    private CreateOrderResponseDto processOrderByUserStatus(CreateOrderRequestDto requestDto,
            String userId, User user, String userRole, String userEmail) {
        if (runningQueue.check(user)) {
            log.info("User {} is in running queue, processing immediate order", userId);
            return processImmediateOrder(requestDto, userId, userRole, userEmail);
        } else if (runningQueue.available()) {
            log.info("Running queue is available, processing queued order for user {}", userId);
            return processQueuedOrder(requestDto, userId, user, userRole, userEmail);
        } else {
            log.info("Running queue is full, processing waiting order for user {}", userId);
            return processWaitingOrder(user);
        }
    }

    // 즉시 주문 처리 - 사용자가 이미 runningQueue에 있을 경우 호출
    private CreateOrderResponseDto processImmediateOrder(CreateOrderRequestDto requestDto,
            String userId, String userRole, String userEmail) {

        // 주문 생성
        Integer totalAmount = getTotalAmount(requestDto.performanceId(),
                requestDto.selectedSeatIds());
        Order order = Order.of(requestDto, userId, totalAmount);

        orderRepository.save(order);

        finishOrder(order.getId(), userId, userRole, userEmail);

        List<SeatDetail> seatDetails = createSeatDetails(requestDto.performanceId(),
                requestDto.selectedSeatIds());
        return CreateOrderResponseDto.from(order, seatDetails);
    }

    private Integer getTotalAmount(UUID performanceId, List<UUID> seatIds) {
        return seatIds.stream().map(
                        seatId -> {
                            SeatInfoResponseDto seatInfo = seatOrderService.getSeatFromRedis(performanceId,
                                    seatId);

                            if (seatInfo.getSeatStatus().equals(SeatStatus.BOOKED)) {
                                throw new SeatException(ExceptionMessage.SEAT_ALREADY_BOOKED);
                            }

                            return seatInfo.getPrice();

                        })
                .reduce(0, Integer::sum);
    }

    // 대기열 주문 처리 - runningQueue에 빈 자리가 있을 경우 호출
    private CreateOrderResponseDto processQueuedOrder(CreateOrderRequestDto requestDto,
            String userId, User user, String userRole, String userEmail) {
        waitingQueue.register(user);
        runningQueue.push(user);

        Integer totalAmount = getTotalAmount(requestDto.performanceId(),
                requestDto.selectedSeatIds());
        Order order = Order.of(requestDto, userId, totalAmount);

        order = orderRepository.save(order);

        finishOrder(order.getId(), userId, userRole, userEmail);

        List<SeatDetail> seatDetails = createSeatDetails(requestDto.performanceId(),
                requestDto.selectedSeatIds());
        return CreateOrderResponseDto.from(order, seatDetails);
    }


    // 대기 상태 처리 - runningQueue에 빈 자리가 없을 경우 호출
    private CreateOrderResponseDto processWaitingOrder(User user) {
        waitingQueue.register(user);
        var waitingTicket = waitingQueue.getTicket(user);
        return CreateOrderResponseDto.waiting(waitingTicket);
    }

    // 좌석 상세 정보 생성
    private List<SeatDetail> createSeatDetails(UUID performanceId, List<UUID> seatIds) {

        return seatIds.stream().map(
                seatId -> {
                    SeatInfoResponseDto seatInfo = seatOrderService.getSeatFromRedis(performanceId,
                            seatId);
                    return new SeatDetail(seatInfo.getSeatId(),
                            seatInfo.getSeatNum(),
                            seatInfo.getSeatRow(),
                            seatInfo.getSeatType()
                    );
                }).toList();
    }

    // 주문 완료 처리 - 결제 처리 및 좌석 상태 변경
    public void finishOrder(UUID orderId, String userId, String userRole, String userEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(ExceptionMessage.ORDER_NOT_FOUND));

        boolean paymentSuccessful = callPaymentService();

        if (paymentSuccessful) {
            performanceClient.confirmSeats(userId, userRole, userEmail,
                    new ConfirmSeatRequestDto(order.getSelectedSeatIds(), order.getId(),
                            order.getPerformanceId()));
            order.setStatus(OrderStatus.COMPLETED);
        } else {
            performanceClient.cancelSeats(userId, userRole, userEmail,
                    new CancelSeatRequestDto(order.getSelectedSeatIds(), order.getId(),
                            order.getPerformanceId()));
            order.setStatus(OrderStatus.FAILED);
        }

        orderRepository.save(order);
        runningQueue.remove(User.of(userId));
    }

    // 대기열에서 실행 큐로 사용자 이동 메서드 주기적으로 실행되어 대기 중인 사용자를 처리
    @Scheduled(fixedRate = 100)
    public void transferWaitingToRunning() {
        while (runningQueue.available()) {
            User user = waitingQueue.pop();
            if (user != null) {
                runningQueue.push(user);
            } else {
                break;
            }
        }
    }

    private boolean callPaymentService() {
        return true;
    }

}