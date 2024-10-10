package com.ticketing.order.application.service;

import com.ticketing.order.application.dto.request.CreateOrderRequestDto;
import com.ticketing.order.application.dto.response.CreateOrderResponseDto;
import com.ticketing.order.application.dto.response.CreateOrderResponseDto.SeatDetail;
import com.ticketing.order.common.exception.OrderException;
import com.ticketing.order.common.response.ExceptionMessage;
import com.ticketing.order.domain.model.Order;
import com.ticketing.order.domain.model.RunningQueue;
import com.ticketing.order.domain.model.User;
import com.ticketing.order.domain.model.WaitingQueue;
import com.ticketing.order.domain.repository.OrderRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RunningQueue runningQueue;
    private final WaitingQueue waitingQueue;


    @Transactional
    public CreateOrderResponseDto createOrder(CreateOrderRequestDto requestDto, String userId) {
        User user = User.of(userId);

        if (runningQueue.check(user)) {
            Order order = Order.of(requestDto, userId);

            orderRepository.save(order);

            finishOrder(order.getId(), userId);

            List<SeatDetail> seatDetails = requestDto.seats().stream()
                    .map(seatId -> new CreateOrderResponseDto.SeatDetail(seatId, null,
                            null))
                    .toList();

            return CreateOrderResponseDto.from(order, seatDetails);
        } else if (runningQueue.available()) {
            waitingQueue.register(user);

            Order order = Order.of(requestDto, userId);

            orderRepository.save(order);

            // 결제 및 큐에서 제거
            finishOrder(order.getId(), userId);

            List<CreateOrderResponseDto.SeatDetail> seatDetails = requestDto.seats().stream()
                    .map(seatId -> new CreateOrderResponseDto.SeatDetail(seatId, null, null))
                    .toList();

            return CreateOrderResponseDto.from(order, seatDetails);
        } else {
            waitingQueue.register(user);
            throw new OrderException(ExceptionMessage.CAPACITY_EXCEED);
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
