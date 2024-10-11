package com.ticketing.order.application.dto.response;

import com.ticketing.order.domain.model.Order;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CreateOrderResponseDto(
        UUID orderId,
        String userId,
        UUID performanceId,
        List<SeatDetail> seats,
        Integer totalAmount,
        String orderStatus,
        String paymentMethod

) {

    public static record SeatDetail(
            UUID seatId,
            Integer seatNum,
            Integer seatRow
    ) {

    }

    public static CreateOrderResponseDto from(Order order,
            List<CreateOrderResponseDto.SeatDetail> seats) {
        return CreateOrderResponseDto.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .performanceId(order.getPerformanceId())
                .seats(seats)
                .totalAmount(order.getTotalAmount())
                .orderStatus(String.valueOf(order.getOrderStatus()))
                .paymentMethod(String.valueOf(order.getPaymentMethod()))

                .build();
    }
}