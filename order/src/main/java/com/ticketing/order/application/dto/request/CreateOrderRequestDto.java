package com.ticketing.order.application.dto.request;

import java.util.List;
import java.util.UUID;

public record CreateOrderRequestDto(
        UUID performanceId,

        List<UUID> selectedSeatIds,

        String paymentMethod
) {
//    public boolean isValid() {
//        return performanceId != null &&
//                selectedSeatIds != null &&
//                !selectedSeatIds.isEmpty() &&
//                paymentMethod != null &&
//                !paymentMethod.trim().isEmpty();
//    }
}