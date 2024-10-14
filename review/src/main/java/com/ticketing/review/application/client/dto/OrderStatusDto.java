package com.ticketing.review.application.client.dto;

import java.util.UUID;

public record OrderStatusDto(
    UUID orderId,
    Long userId,
    UUID performanceId,
    String orderStatus
) {

  // TODO Order 개발 후 toOrderStatusDto 구현 예정
}
