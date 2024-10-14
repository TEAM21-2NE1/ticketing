package com.ticketing.performance.application.dto.performance;

import com.ticketing.performance.domain.model.Performance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class UpdatePrfResponseDto {

    private UUID performanceId;
    private String title;

    public static UpdatePrfResponseDto of(Performance performance) {
        return UpdatePrfResponseDto.builder()
                .performanceId(performance.getId())
                .title(performance.getTitle())
                .build();
    }

}
