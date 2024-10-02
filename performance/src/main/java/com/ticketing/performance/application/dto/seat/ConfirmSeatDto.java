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
public class ConfirmSeatDto {

    private UUID seatId;
    private SeatStatus seatStatus;
    private UUID orderId;

    public static ConfirmSeatDto of(Seat seat) {
        return ConfirmSeatDto.builder()
                .seatId(seat.getId())
                .seatStatus(seat.getSeatStatus())
                .orderId(seat.getOrderId())
                .build();
    }
}
