package com.ticketing.order.application.service;

import com.ticketing.order.application.dto.client.CancelSeatRequestDto;
import com.ticketing.order.application.dto.client.ConfirmSeatRequestDto;
import com.ticketing.order.application.dto.client.SeatInfoResponseDto;
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
import java.util.UUID;
import java.util.concurrent.TimeUnit;
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
            log.info("Creating order for user: {}", userId);

            // 공연 정보
            performanceClient.getPerformance(userId, userRole, userEmail,
                    requestDto.performanceId());

            // 좌석 선택 가능 여부는 이전 좌석 선택 단계에서 진행 예정이라 주석 처리함
            // 좌석 선택 가능 여부 확인 및 임시 점유
//            List<UUID> selectedSeatIds = requestDto.selectedSeatIds();
//            boolean seatsAvailable = checkAndHoldSeats(requestDto.performanceId(), selectedSeatIds,
//                    userId);
//            if (!seatsAvailable) {
//                throw new OrderException(ExceptionMessage.SEAT_UNAVAILABLE);
//            }

            // 1. 사용자 상태에 따른 처리
            if (runningQueue.check(user)) {
                // 사용자는 running queue에 있음 -> 주문 생성 진행
                log.info("User {} is in running queue, processing immediate order", userId);
                return processImmediateOrder(requestDto, userId, userRole, userEmail);

            } else if (runningQueue.available()) {

                // 사용자는 running:queue에 없지만, 현재 running queue에 자리 있어서 주문 생성 진행
                log.info("Running queue is available, processing queued order for user {}", userId);
                return processQueuedOrder(requestDto, userId, user, userRole, userEmail);

            } else {
                // running queue가 차서, waiting queue로 들어가는 프로세스 진행
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

        // 주문 생성

        Integer totalAmount = getTotalAmount(requestDto.performanceId(),
                requestDto.selectedSeatIds());
        Order order = Order.of(requestDto, userId, totalAmount);

        orderRepository.save(order);

        // 결제 요청은 아직 진행하지 않음
//        finishOrder(order.getId(), userId, userRole, userEmail);

        List<SeatDetail> seatDetails = createSeatDetails(requestDto.performanceId(),
                requestDto.selectedSeatIds());
        return CreateOrderResponseDto.from(order, seatDetails);
    }

    private Integer getTotalAmount(UUID performanceId, List<UUID> seatIds) {
        return seatIds.stream().map(
                        seatId -> {
                            SeatInfoResponseDto seatInfo = seatOrderService.getSeatFromRedis(performanceId,
                                    seatId);
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

    // 좌석 임시 점유 확인 및 설정
    private boolean checkAndHoldSeats(UUID performanceId, List<UUID> seatIds, String userId) {
        String keyPrefix = "performance:" + performanceId + ":seats";

        return seatIds.stream().allMatch(seatId -> {
            String seatKey = keyPrefix + ":" + seatId;
            String seatStatus = String.valueOf(redisTemplate.opsForValue().get(seatKey));

            if (seatStatus == null) {
                throw new IllegalStateException("해당 좌석에 대한 정보가 없습니다: " + seatId);
            }

            switch (seatStatus) {
                case "BOOKED":
                case "HOLD":
                    return false;
                case "AVAILABLE":
                    // AVAILABLE 상태일 경우 해당 좌석을 임시로 점유하고 5분 타임아웃 설정
                    redisTemplate.opsForValue().set(seatKey, User.of(userId), 5, TimeUnit.MINUTES);
                    return true;
                default:
                    throw new IllegalStateException("알 수 없는 좌석 상태: " + seatStatus);
            }
        });
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

    // TODO
    private boolean callPaymentService() {
        return true;
    }
}