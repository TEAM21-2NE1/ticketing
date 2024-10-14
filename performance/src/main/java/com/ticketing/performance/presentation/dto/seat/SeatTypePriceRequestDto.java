package com.ticketing.performance.presentation.dto.seat;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SeatTypePriceRequestDto {
        @NotBlank
        private String seatType;
        @NotBlank
        private Integer price;
}

