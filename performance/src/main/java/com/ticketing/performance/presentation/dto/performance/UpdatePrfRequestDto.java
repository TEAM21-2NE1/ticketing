package com.ticketing.performance.presentation.dto.performance;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    private UUID hallId;
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotBlank
    private Integer runningTime;
    @NotBlank
    private Integer intermission;
    @NotBlank
    private Integer ageLimit;
    @NotBlank
    private LocalDate openDate;
    @NotBlank
    private LocalDateTime performanceTime;
    @NotBlank
    private LocalDateTime ticketOpenTime;
    @NotBlank
    private Integer ticketLimit;
}
