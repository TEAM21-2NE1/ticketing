package com.ticketing.performance.presentation.dto.seat;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateSeatPriceRequestDto {

    @NotBlank
    private UUID performanceId;
    @NotBlank
    private String seatType;
    @NotBlank
    private Integer price;
}
