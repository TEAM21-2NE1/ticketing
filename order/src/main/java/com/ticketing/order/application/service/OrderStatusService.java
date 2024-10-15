package com.ticketing.order.application.service;

import com.ticketing.order.application.dto.client.ConfirmSeatRequestDto;
import com.ticketing.order.common.exception.OrderException;
import com.ticketing.order.common.response.ExceptionMessage;
import com.ticketing.order.config.SecurityUtil;
import com.ticketing.order.domain.model.Order;
import com.ticketing.order.domain.model.OrderStatus;
import com.ticketing.order.domain.model.RunningQueue;
import com.ticketing.order.domain.model.User;
import com.ticketing.order.domain.repository.OrderRepository;
import com.ticketing.order.infrastructure.PerformanceClient;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderStatusService {

    private final OrderRepository orderRepository;
    private final SeatOrderService seatOrderService;
    private final PerformanceClient performanceClient;
    private final RunningQueue runningQueue;

    @Transactional
    public void changeOrderBySuccess(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new OrderException(ExceptionMessage.ORDER_NOT_FOUND));

        order.setStatus(OrderStatus.COMPLETED);
        List<UUID> seatIds = order.getSelectedSeatIds();
        UUID performanceId = order.getPerformanceId();

        seatOrderService.confirm(seatIds, performanceId);

        ConfirmSeatRequestDto requestDto = new ConfirmSeatRequestDto(seatIds, orderId, performanceId);
        performanceClient.confirmSeats(SecurityUtil.getId().toString(),
                SecurityUtil.getRole(),
                SecurityUtil.getEmail(),
                requestDto);

        runningQueue.remove(User.of(SecurityUtil.getId().toString()));
    }

}
