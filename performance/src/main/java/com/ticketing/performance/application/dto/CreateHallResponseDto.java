package com.ticketing.performance.application.dto;

import com.ticketing.performance.domain.model.Hall;
import com.ticketing.performance.domain.model.HallSeat;
import lombok.*;

import java.util.List;
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
                .totalSeat(
                        hall.getHallSeats()
                                .stream()
                                .mapToInt(i -> i.getSeatsPerRow() * i.getRows())
                                .sum()
                )
                .build();
    }
}
