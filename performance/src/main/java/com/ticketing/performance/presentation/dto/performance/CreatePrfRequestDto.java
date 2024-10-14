package com.ticketing.performance.presentation.dto.performance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreatePrfRequestDto {

    @NotNull
    private UUID hallId;
    @NotBlank
    private String title;
    @NotBlank
    private String description;

    private Integer runningTime;
    private Integer intermission;
    private Integer ageLimit;
    private LocalDate openDate;
    private LocalDateTime performanceTime;
    private LocalDateTime ticketOpenTime;
    private Integer ticketLimit;
    private MultipartFile image;
}
