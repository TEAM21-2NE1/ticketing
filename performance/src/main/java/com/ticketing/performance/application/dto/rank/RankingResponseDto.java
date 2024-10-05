package com.ticketing.performance.application.dto.rank;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Builder
public class RankingResponseDto {

    private UUID performanceId;
    private String title;
    private String posterUrl;
    private LocalDateTime performanceTime;
    private Double reservationRate;
    private Integer ranking;

    @QueryProjection
    public RankingResponseDto(UUID performanceId, String title, String posterUrl, LocalDateTime performanceTime, Double reservationRate, Integer ranking) {
        this.performanceId = performanceId;
        this.title = title;
        this.posterUrl = posterUrl;
        this.performanceTime = performanceTime;
        this.reservationRate = reservationRate;
        this.ranking = ranking;
    }
}
