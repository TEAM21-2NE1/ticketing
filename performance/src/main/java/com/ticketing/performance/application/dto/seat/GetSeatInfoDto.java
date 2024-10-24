package com.ticketing.performance.application.dto.seat;

import com.ticketing.performance.domain.model.Seat;
import com.ticketing.performance.domain.model.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class GetSeatInfoDto implements Serializable {

    private SeatStatus seatStatus;

    public static GetSeatInfoDto of(Seat seat) {
        return GetSeatInfoDto.builder()
                .seatStatus(seat.getSeatStatus())
                .build();
    }

}

