package com.ticketing.performance.presentation.dto.performance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdatePrfRequestDto {

    private UUID hallId;
    private String title;
    private String description;
    private Integer runningTime;
    private Integer intermission;
    private Integer ageLimit;
    private LocalDate openDate;
    private LocalDateTime performanceTime;
    private LocalDateTime ticketOpenTime;
    private Integer ticketLimit;
}
