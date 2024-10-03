package com.ticketing.performance.application.dto.seat;

import com.ticketing.performance.domain.model.Seat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ConfirmSeatResponseDto {

    private List<ConfirmSeatDto> confirmedSeats;

    public static ConfirmSeatResponseDto of(List<Seat> seats) {
        return ConfirmSeatResponseDto.builder()
                .confirmedSeats(seats.stream().map(ConfirmSeatDto::of).toList())
                .build();

    }
}
