package com.ticketing.review.application.client.dto;

import com.ticketing.review.infrastructure.client.dto.PrfInfoResponseDto;
import java.time.LocalDateTime;
import java.util.UUID;

public record PerformanceInfoDto(
    UUID performanceId,
    LocalDateTime performanceTime
) {

  public static PerformanceInfoDto toPerformanceInfoDto(PrfInfoResponseDto prfInfoResponseDto) {
    return new PerformanceInfoDto(prfInfoResponseDto.performanceId(),
        prfInfoResponseDto.performanceTime());
  }
}
