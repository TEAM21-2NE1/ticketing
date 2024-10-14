package com.ticketing.performance.application.dto.performance;

import com.ticketing.performance.domain.model.Performance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class PrfListResponseDto {

    private UUID hallId;
    private UUID performanceId;
    private String title;
    private Integer ageLimit;
    private LocalDate openDate;
    private LocalDateTime performanceTime;
    private LocalDateTime ticketOpenTime;


    public static PrfListResponseDto of(Performance performance) {
        return PrfListResponseDto.builder()
                .hallId(performance.getHallId())
                .performanceId(performance.getId())
                .title(performance.getTitle())
                .ageLimit(performance.getAgeLimit())
                .openDate(performance.getOpenDate())
                .performanceTime(performance.getPerformanceTime())
                .ticketOpenTime(performance.getTicketOpenTime())
                .build();

    }
}
