package com.ticketing.performance.application.dto.seat;

import com.ticketing.performance.domain.model.Seat;
import com.ticketing.performance.domain.model.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CancelSeatDto {

    private UUID seatId;
    private SeatStatus seatStatus;

    public static CancelSeatDto of(Seat seat) {
        return CancelSeatDto.builder()
                .seatId(seat.getId())
                .seatStatus(seat.getSeatStatus())
                .build();
    }
}
