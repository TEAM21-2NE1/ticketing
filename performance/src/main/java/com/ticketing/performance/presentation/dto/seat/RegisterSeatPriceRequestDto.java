package com.ticketing.performance.presentation.dto.seat;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RegisterSeatPriceRequestDto {

    private UUID performanceId;
    private List<SeatTypePriceRequestDto> sections;



}
