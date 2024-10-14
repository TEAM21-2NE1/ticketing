package com.ticketing.order.application.service;

import com.ticketing.order.application.dto.client.CancelSeatRequestDto;
import com.ticketing.order.application.dto.client.ConfirmSeatRequestDto;
import com.ticketing.order.application.dto.client.PrfInfoResponseDto;
import com.ticketing.order.application.dto.request.CreateOrderRequestDto;
import com.ticketing.order.application.dto.response.CreateOrderResponseDto;
import com.ticketing.order.application.dto.response.CreateOrderResponseDto.SeatDetail;
import com.ticketing.order.common.exception.OrderException;
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
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
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

    private final ReentrantLock lock = new ReentrantLock();

    // 주문 생성 - 사용자의 상태에 따라 즉시 처리, 대기열 처리, 또는 대기 상태로 전환
    @Transactional
    public CreateOrderResponseDto createOrder(CreateOrderRequestDto requestDto, String userId,
            String userRole, String userEmail) {
        lock.lock();
        try {
            User user = User.of(userId);
            log.info("Creating order for user: {}", userId);

            // 공연 정보
            performanceClient.getPerformance(Long.valueOf(userId), userRole, userEmail,
                    requestDto.performanceId());

            // 좌석 선택 가능 여부 확인 및 임시 점유
            List<UUID> selectedSeatIds = requestDto.selectedSeatIds();
            boolean seatsAvailable = checkAndHoldSeats(requestDto.performanceId(), selectedSeatIds,
                    userId);

            if (!seatsAvailable) {
                throw new OrderException(ExceptionMessage.SEAT_UNAVAILABLE);
            }

            // 사용자 상태에 따른 처리
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
        } finally {
            lock.unlock();
        }
    }


    // 즉시 주문 처리 - 사용자가 이미 runningQueue에 있을 경우 호출
    private CreateOrderResponseDto processImmediateOrder(CreateOrderRequestDto requestDto,
            String userId, String userRole, String userEmail) {
        Order order = Order.of(requestDto, userId);
        log.info("User {} is already in running queue", userId);

        orderRepository.save(order);
        finishOrder(order.getId(), userId, userRole, userEmail);

        List<SeatDetail> seatDetails = createSeatDetails(requestDto.performanceId(),
                requestDto.selectedSeatIds(), userId, userRole, userEmail);
        return CreateOrderResponseDto.from(order, seatDetails);
    }

    // 대기열 주문 처리 - runningQueue에 빈 자리가 있을 경우 호출
    private CreateOrderResponseDto processQueuedOrder(CreateOrderRequestDto requestDto,
            String userId, User user, String userRole, String userEmail) {
        waitingQueue.register(user);
        runningQueue.push(user);

        Order order = Order.of(requestDto, userId);
        log.info("User {} added to running queue", userId);

        orderRepository.save(order);
        finishOrder(order.getId(), userId, userRole, userEmail);

        List<SeatDetail> seatDetails = createSeatDetails(requestDto.performanceId(),
                requestDto.selectedSeatIds(), userId, userRole, userEmail);
        return CreateOrderResponseDto.from(order, seatDetails);
    }


    // 대기 상태 처리 - runningQueue에 빈 자리가 없을 경우 호출
    private CreateOrderResponseDto processWaitingOrder(User user) {
        log.info("Processing waiting order for user: {}", user.getUserId());
        waitingQueue.register(user);
        var waitingTicket = waitingQueue.getTicket(user);
        log.info("User {} added to waiting queue with number {}", user.getUserId(),
                waitingTicket.getOrder());
        return CreateOrderResponseDto.waiting(waitingTicket);
    }

    // 좌석 상세 정보 생성
    private List<SeatDetail> createSeatDetails(UUID performanceId, List<UUID> seatIds,
            String userId, String userRole, String userEmail) {
        PrfInfoResponseDto performanceInfo = performanceClient.getPerformance(
                Long.parseLong(userId), userRole, userEmail, performanceId
        ).data();

        Map<UUID, PrfInfoResponseDto.SeatInfo> seatInfoMap = performanceInfo.getSeats().stream()
                .collect(Collectors.toMap(PrfInfoResponseDto.SeatInfo::getId, Function.identity()));

        return seatIds.stream()
                .map(seatId -> {
                    PrfInfoResponseDto.SeatInfo seatInfo = seatInfoMap.get(seatId);
                    return new SeatDetail(
                            seatId,
                            seatInfo.getSeatNum(),
                            seatInfo.getSeatRow(),
                            seatInfo.getSeatType()
                    );
                })
                .collect(Collectors.toList());
    }

    // 좌석 임시 점유 확인 및 설정
    private boolean checkAndHoldSeats(UUID performanceId, List<UUID> seatIds, String userId) {
        String key = "performance:" + performanceId + ":seats";
        return seatIds.stream().allMatch(seatId -> {
            Boolean result = redisTemplate.opsForValue().setIfAbsent(key + ":" + seatId,
                    User.of(userId), 5, TimeUnit.MINUTES);
            return Boolean.TRUE.equals(result);
        });
    }

    // 주문 완료 처리 - 결제 처리 및 좌석 상태 변경
    public void finishOrder(UUID orderId, String userId, String userRole, String userEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(ExceptionMessage.ORDER_NOT_FOUND));

        boolean paymentSuccessful = callPaymentService();

        if (paymentSuccessful) {
            performanceClient.confirmSeats(Long.parseLong(userId), userRole, userEmail,
                    new ConfirmSeatRequestDto(order.getSelectedSeatIds(), order.getId(),
                            order.getPerformanceId()));
            order.setStatus(OrderStatus.COMPLETED);
        } else {
            performanceClient.cancelSeats(Long.parseLong(userId), userRole, userEmail,
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

    // TODO
    private boolean callPaymentService() {
        return true;
    }
}