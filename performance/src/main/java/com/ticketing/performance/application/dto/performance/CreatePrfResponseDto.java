package com.ticketing.performance.application.dto.performance;

import com.ticketing.performance.domain.model.Performance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CreatePrfResponseDto {

    private UUID performanceId;
    private UUID hallId;
    private String title;

    public static CreatePrfResponseDto of(Performance performance) {
        return CreatePrfResponseDto.builder()
                .performanceId(performance.getId())
                .hallId(performance.getHallId())
                .title(performance.getTitle())
                .build();
    }
}
