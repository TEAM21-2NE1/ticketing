package com.ticketing.performance.application.dto.performance;

import com.ticketing.performance.domain.model.Performance;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class PrfRedisInfoDto {

    private UUID performanceId;
    private LocalDateTime performanceTime;
    private LocalDateTime ticketOpenTime;
    private int ticketLimit;

    public static PrfRedisInfoDto of(Performance performance) {
        return PrfRedisInfoDto.builder()
                .performanceId(performance.getId())
                .performanceTime(performance.getPerformanceTime())
                .ticketOpenTime(performance.getTicketOpenTime())
                .ticketLimit(performance.getTicketLimit())
                .build();
    }
}
