package com.ticketing.review.infrastructure.client.dto;

import com.ticketing.review.application.client.dto.PerformanceInfoDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


public record PrfInfoResponseDto(
    UUID performanceId,
    UUID hallId,
    String hallName,
    Long managerId,
    String title,
    String posterUrl,
    String description,
    Integer runningTime,
    Integer intermission,
    Integer ageLimit,
    LocalDate openDate,
    LocalDateTime performanceTime,
    LocalDateTime ticketOpenTime,
    Integer ticketLimit,
    Integer totalSeat,
    Integer availableSeat
) {

  public PerformanceInfoDto toPerformanceInfoDto() {
    return new PerformanceInfoDto(this.performanceId(),
        this.performanceTime());
  }
}
