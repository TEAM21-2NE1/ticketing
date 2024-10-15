package com.ticketing.order.application.dto.response;

import com.ticketing.order.domain.model.Order;
import lombok.Builder;

@Builder
public record GetOrderStatusResponse(
        String orderStatus
) {

    public static GetOrderStatusResponse from(Order order) {
        return GetOrderStatusResponse.builder()
                .orderStatus(String.valueOf(order.getOrderStatus()))
                .build();
    }
}
