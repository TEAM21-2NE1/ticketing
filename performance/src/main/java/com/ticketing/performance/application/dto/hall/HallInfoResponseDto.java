package com.ticketing.performance.application.dto.hall;

import com.ticketing.performance.domain.model.Hall;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class HallInfoResponseDto implements Serializable {

    private UUID hallId;
    private String hallName;
    private String hallAddress;
    private Integer totalSeat;
    private List<HallSeatInfoResponseDto> seats;

    public static HallInfoResponseDto of(Hall hall) {
        List<HallSeatInfoResponseDto> seats = hall.getHallSeats().stream().map(HallSeatInfoResponseDto::of).toList();

        return HallInfoResponseDto.builder()
                .hallId(hall.getId())
                .hallAddress(hall.getHallAddress())
                .hallName(hall.getHallName())
                .totalSeat(hall.getTotalSeat())
                .seats(seats)
                .build();
    }
}
