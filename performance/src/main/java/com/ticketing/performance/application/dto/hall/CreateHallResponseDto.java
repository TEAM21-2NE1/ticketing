package com.ticketing.performance.application.dto.hall;

import com.ticketing.performance.domain.model.Hall;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class CreateHallResponseDto {

    private UUID hallId;
    private String hallName;
    private String hallAddress;
    private Integer totalSeat;

    public static CreateHallResponseDto of(Hall hall) {
        return CreateHallResponseDto.builder()
                .hallId(hall.getId())
                .hallName(hall.getHallName())
                .hallAddress(hall.getHallAddress())
                .totalSeat(hall.getTotalSeat())
                .build();
    }
}
