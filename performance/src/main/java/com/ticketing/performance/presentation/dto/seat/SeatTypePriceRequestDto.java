package com.ticketing.performance.presentation.dto.seat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SeatTypePriceRequestDto {
        private String seatType;
        private Integer price;
}

