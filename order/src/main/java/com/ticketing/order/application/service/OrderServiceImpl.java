package com.ticketing.order.application.service;

import com.ticketing.order.application.dto.client.CancelSeatRequestDto;
import com.ticketing.order.application.dto.client.CancelSeatResponseDto;
import com.ticketing.order.application.dto.client.ConfirmSeatRequestDto;
import com.ticketing.order.application.dto.client.ConfirmSeatResponseDto;
import com.ticketing.order.application.dto.client.HoldSeatRequestDto;
import com.ticketing.order.application.dto.client.PrfInfoResponseDto;
import com.ticketing.order.application.dto.request.CreateOrderRequestDto;
import com.ticketing.order.application.dto.response.CreateOrderResponseDto;
import com.ticketing.order.application.dto.response.CreateOrderResponseDto.SeatDetail;
import com.ticketing.order.common.exception.OrderException;
import com.ticketing.order.common.response.ExceptionMessage;
import com.ticketing.order.common.response.SuccessResponse;
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

    @Transactional
    public CreateOrderResponseDto createOrder(CreateOrderRequestDto requestDto, String userId,
            String userRole, String email) {
        lock.lock();
        try {
            User user = User.of(userId);

            // 공연 정보
            performanceClient.getPerformance(Long.parseLong(userId), userRole, email,
                    requestDto.performanceId());

            // 좌석 선택 가능 여부 확인 및 임시 점유
            List<UUID> selectedSeatIds = requestDto.selectedSeatIds();
            boolean seatsAvailable = checkAndHoldSeats(requestDto.performanceId(), selectedSeatIds,
                    userId, userRole, email);

            if (!seatsAvailable) {
                throw new OrderException(ExceptionMessage.SEAT_UNAVAILABLE);
            }

            // 사용자가 이미 runningQueue에 있을 경우 예매 즉시 처리
            if (runningQueue.check(user)) {
                return processImmediateOrder(requestDto, userId, userRole, email, selectedSeatIds);
            }
            // runningQueue에 빈 자리가 있으면 사용자를 대기열에 추가하고 예매 처리
            else if (runningQueue.available()) {
                return processQueuedOrder(requestDto, userId, userRole, email, user,
                        selectedSeatIds);
            }
            // 빈 자리가 없으므로 대기열에 등록하고, 대기 번호를 응답으로 반환
            else {
                return processWaitingOrder(user);
            }
        } finally {
            lock.unlock();
        }
    }

    // 즉시 예매 처리 메소드
    private CreateOrderResponseDto processImmediateOrder(CreateOrderRequestDto requestDto,
            String userId, String userRole, String email, List<UUID> selectedSeatIds) {
        Order order = Order.of(requestDto, userId);

        orderRepository.save(order);
        // 좌석 임시 점유 상태로 변경
        holdSeats(requestDto.performanceId(), selectedSeatIds, userId, userRole, email);
        finishOrder(order.getId(), userId, userRole, email);

        List<SeatDetail> seatDetails = createSeatDetails(requestDto.performanceId(), userId,
                userRole, email, selectedSeatIds);
        return CreateOrderResponseDto.from(order, seatDetails);
    }

    // 대기열에서 예매 처리 메소드
    private CreateOrderResponseDto processQueuedOrder(CreateOrderRequestDto requestDto,
            String userId, String userRole, String email, User user, List<UUID> selectedSeatIds) {
        waitingQueue.register(user);
        runningQueue.push(user);

        Order order = Order.of(requestDto, userId);

        orderRepository.save(order);
        // 좌석 임시 점유 상태로 변경
        holdSeats(requestDto.performanceId(), selectedSeatIds, userId, userRole, email);
        finishOrder(order.getId(), userId, userRole, email);

        List<SeatDetail> seatDetails = createSeatDetails(requestDto.performanceId(), userId,
                userRole, email, selectedSeatIds);
        return CreateOrderResponseDto.from(order, seatDetails);
    }

    // 대기열 등록
    private CreateOrderResponseDto processWaitingOrder(User user) {
        waitingQueue.register(user);
        var waitingTicket = waitingQueue.getTicket(user);
        return CreateOrderResponseDto.waiting(waitingTicket);
    }

    // 수정된 좌석 상세 정보 생성
    private List<SeatDetail> createSeatDetails(UUID performanceId, String userId, String userRole,
            String email, List<UUID> seatIds) {

        PrfInfoResponseDto performanceInfo = performanceClient.getPerformance(
                Long.parseLong(userId), userRole, email, performanceId
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

    // Redis를 사용하여 좌석 임시 점유 상태 확인 및 설정
    private boolean checkAndHoldSeats(UUID performanceId, List<UUID> seatIds, String userId,
            String userRole, String email) {
        String key = "performance:" + performanceId + ":seats";
        return seatIds.stream().allMatch(seatId -> {
            // User ID만 저장하는 경우
            Boolean result = redisTemplate.opsForValue().setIfAbsent(key + ":" + seatId,
                    User.of(userId), 5, TimeUnit.MINUTES);
            return Boolean.TRUE.equals(result);
        });
    }
    // 좌석 임시 점유
    private void holdSeats(UUID performanceId, List<UUID> seatIds, String userId, String userRole,
            String email) {
        for (UUID seatId : seatIds) {
            HoldSeatRequestDto holdRequest = new HoldSeatRequestDto(performanceId, seatId);
            performanceClient.holdSeats(Long.parseLong(userId), userRole, email, holdRequest);
        }
    }

    public void finishOrder(UUID orderId, String userId, String userRole, String email) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(ExceptionMessage.ORDER_NOT_FOUND));

        boolean paymentSuccessful = callPaymentService();

        if (paymentSuccessful) {
            // 결제 성공 시 좌석 상태를 확정으로 변경
            ConfirmSeatRequestDto confirmRequest = new ConfirmSeatRequestDto(
                    order.getSelectedSeatIds(),
                    order.getId(),
                    order.getPerformanceId()
            );
            SuccessResponse<ConfirmSeatResponseDto> confirmResponse = performanceClient.confirmSeats(
                    Long.parseLong(userId), userRole, email, confirmRequest
            );
            order.setStatus(OrderStatus.COMPLETED);
        } else {
            // 결제 실패 시 좌석 상태를 취소로 변경
            CancelSeatRequestDto cancelRequest = new CancelSeatRequestDto(
                    order.getSelectedSeatIds(),
                    order.getId(),
                    order.getPerformanceId()
            );
            SuccessResponse<CancelSeatResponseDto> cancelResponse = performanceClient.cancelSeats(
                    Long.parseLong(userId), userRole, email, cancelRequest
            );
            order.setStatus(OrderStatus.FAILED);
        }

        orderRepository.save(order);
        runningQueue.remove(User.of(userId));
    }

    @Scheduled(fixedRate = 60000)
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