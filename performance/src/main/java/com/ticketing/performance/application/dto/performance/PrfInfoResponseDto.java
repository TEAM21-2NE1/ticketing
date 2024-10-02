package com.ticketing.performance.application.dto.performance;

import com.ticketing.performance.domain.model.Performance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class PrfInfoResponseDto {

    private UUID performanceId;
    private UUID hallId;
    private String hallName;
    private Long managerId;
    private String title;
    private String posterUrl;
    private String description;
    private Integer runningTime;
    private Integer intermission;
    private Integer ageLimit;
    private LocalDate openDate;
    private LocalDateTime performanceTime;
    private LocalDateTime ticketOpenTime;
    private Integer ticketLimit;
    private Integer totalSeat;
    private Integer availableSeat;

    public static PrfInfoResponseDto of(Performance performance, String hallName, int totalSeat, int availableSeat) {
        return PrfInfoResponseDto.builder()
                .performanceId(performance.getId())
                .hallId(performance.getHallId())
                .hallName(hallName)
                .managerId(performance.getManagerId())
                .title(performance.getTitle())
                .posterUrl(performance.getPosterUrl())
                .description(performance.getDescription())
                .runningTime(performance.getRunningTime())
                .intermission(performance.getIntermission())
                .ageLimit(performance.getAgeLimit())
                .openDate(performance.getOpenDate())
                .performanceTime(performance.getPerformanceTime())
                .ticketOpenTime(performance.getTicketOpenTime())
                .ticketLimit(performance.getTicketLimit())
                .totalSeat(totalSeat)
                .availableSeat(availableSeat)
                .build();
    }
}
