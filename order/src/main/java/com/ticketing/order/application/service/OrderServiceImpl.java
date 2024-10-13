package com.ticketing.order.application.service;

import com.ticketing.order.application.dto.request.CreateOrderRequestDto;
import com.ticketing.order.application.dto.response.CreateOrderResponseDto;
import com.ticketing.order.application.dto.response.CreateOrderResponseDto.SeatDetail;
import com.ticketing.order.domain.model.Order;
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

    private final ReentrantLock lock = new ReentrantLock();

    @Transactional
    public CreateOrderResponseDto createOrder(CreateOrderRequestDto requestDto, String userId) {
        lock.lock();
        try {
            User user = User.of(userId);

            // 사용자가 이미 runningQueue에 있을 경우 주문을 즉시 처리
            if (runningQueue.check(user)) {
                Order order = Order.of(requestDto, userId);
                log.info("User {} is already in running queue", userId);

                orderRepository.save(order);

                finishOrder(order.getId(), userId);

                List<SeatDetail> seatDetails = requestDto.seats().stream()
                        .map(seatId -> new CreateOrderResponseDto.SeatDetail(seatId, null, null))
                        .toList();

                return CreateOrderResponseDto.from(order, seatDetails);
            }
            // runningQueue에 빈 자리가 있으면 사용자를 대기열에 추가하고 주문 처리
            else if (runningQueue.available()) {
                waitingQueue.register(user);
                runningQueue.push(user);

                Order order = Order.of(requestDto, userId);
                log.info("User {} added to running queue", userId);

                orderRepository.save(order);

                // 결제 및 큐에서 제거
                finishOrder(order.getId(), userId);

                List<CreateOrderResponseDto.SeatDetail> seatDetails = requestDto.seats().stream()
                        .map(seatId -> new CreateOrderResponseDto.SeatDetail(seatId, null, null))
                        .toList();

                return CreateOrderResponseDto.from(order, seatDetails);
            }
            // 빈 자리가 없으므로 대기열에 등록하고, 대기 번호를 응답으로 반환
            else {
                waitingQueue.register(user);
                var waitingNumber = waitingQueue.getTicket(user);
                log.info("User {} added to waiting queue with number {}", userId, waitingNumber);
                return CreateOrderResponseDto.waiting(waitingNumber);
            }
        } finally {
            lock.unlock();
        }
    }

    public void finishOrder(UUID orderId, String userId) {
        callPaymentService();
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
