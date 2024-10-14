package com.ticketing.order.application.dto.request;

import java.util.List;
import java.util.UUID;

public record CreateOrderRequestDto(
        UUID performanceId,
        List<UUID> seats,
        String paymentMethod
) {

}