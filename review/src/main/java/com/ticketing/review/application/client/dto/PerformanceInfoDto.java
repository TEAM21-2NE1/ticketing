package com.ticketing.review.application.client.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PerformanceInfoDto(
    UUID performanceId,
    LocalDateTime performanceTime
) {

}
