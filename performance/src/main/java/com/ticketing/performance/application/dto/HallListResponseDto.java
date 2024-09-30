package com.ticketing.performance.application.dto;

import com.ticketing.performance.domain.model.Hall;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class HallListResponseDto {

    private UUID hallId;
    private String hallName;
    private String hallAddress;
    private Integer totalSeat;

    public static HallListResponseDto of(Hall hall) {
        return HallListResponseDto.builder()
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
