package com.ticketing.performance.presentation.dto.seat;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateSeatPriceRequestDto {

    @NotNull
    private UUID performanceId;
    @NotBlank
    private String seatType;
    @NotNull
    private Integer price;
}
