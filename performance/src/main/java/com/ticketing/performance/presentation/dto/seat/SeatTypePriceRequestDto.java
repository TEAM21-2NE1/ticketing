package com.ticketing.performance.presentation.dto.seat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SeatTypePriceRequestDto {
        @NotBlank
        private String seatType;
        @NotNull
        private Integer price;
}

