package com.ticketing.performance.presentation.dto.seat;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateSeatPriceRequestDto {

    private UUID performanceId;
    private String seatType;
    private Integer price;
}
