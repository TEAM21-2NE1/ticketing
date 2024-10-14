package com.ticketing.performance.application.dto.seat;


import com.ticketing.performance.domain.model.Seat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CancelSeatResponseDto {

    private List<CancelSeatDto> canceledSeats;

    public static CancelSeatResponseDto of(List<Seat> seats) {
        return CancelSeatResponseDto.builder()
                .canceledSeats(seats.stream().map(CancelSeatDto::of).toList())
                .build();
    }
}
