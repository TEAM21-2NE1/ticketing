package com.ticketing.order.application.dto.response;

import com.ticketing.order.domain.model.Order;
import com.ticketing.order.domain.model.OrderStatus;
import com.ticketing.order.domain.model.PaymentMethod;
import lombok.Builder;

import java.util.UUID;

@Builder
public record GetOrderListResponseDto(
        UUID orderId,
        String userId,
        UUID performanceId,
        Integer totalAmount,
        OrderStatus orderStatus,
        PaymentMethod paymentMethod
) {
    public static GetOrderListResponseDto from(Order order) {
        return GetOrderListResponseDto.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .performanceId(order.getPerformanceId())
                .totalAmount(order.getTotalAmount())
                .orderStatus(order.getOrderStatus())
                .paymentMethod(order.getPaymentMethod())
                .build();
    }


}
