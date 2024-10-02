package com.ticketing.performance.application.dto.hall;

import com.ticketing.performance.domain.model.Hall;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UpdateHallResponseDto {

    private UUID hallId;
    private String hallName;
    private String hallAddress;
    private Integer totalSeat;

    public static UpdateHallResponseDto of(Hall hall) {
        return UpdateHallResponseDto.builder()
                .hallId(hall.getId())
                .hallName(hall.getHallName())
                .hallAddress(hall.getHallAddress())
                .totalSeat(hall.getTotalSeat())
                .build();
    }

}
