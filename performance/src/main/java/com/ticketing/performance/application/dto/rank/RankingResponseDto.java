package com.ticketing.performance.application.dto.rank;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class RankingResponseDto implements Serializable {

    private UUID performanceId;
    private String title;
    private String posterUrl;
    private String performanceTime;
    private Double reservationRate;
    @Setter
    private Integer ranking;

    @QueryProjection
    public RankingResponseDto(UUID performanceId, String title, String posterUrl, LocalDateTime performanceTime, Double reservationRate) {
        this.performanceId = performanceId;
        this.title = title;
        this.posterUrl = posterUrl;
        this.performanceTime = performanceTime.toString();
        this.reservationRate = reservationRate;
    }

}
