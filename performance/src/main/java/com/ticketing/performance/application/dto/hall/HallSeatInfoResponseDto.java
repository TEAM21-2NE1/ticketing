package com.ticketing.performance.application.dto.hall;

import com.ticketing.performance.domain.model.HallSeat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class HallSeatInfoResponseDto implements Serializable {

    private UUID hallSeatId;
    private String seatType;
    private Integer rows;
    private Integer seatsPerRow;

    public static HallSeatInfoResponseDto of(HallSeat hallSeat) {
        return HallSeatInfoResponseDto.builder()
                .hallSeatId(hallSeat.getId())
                .seatType(hallSeat.getSeatType())
                .rows(hallSeat.getRows())
                .seatsPerRow(hallSeat.getSeatsPerRow())
                .build();
    }
}
