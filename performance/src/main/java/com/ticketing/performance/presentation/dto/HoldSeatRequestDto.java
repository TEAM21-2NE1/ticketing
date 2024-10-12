package com.ticketing.performance.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class HoldSeatRequestDto {

    private UUID performanceId;
    private UUID seatId;
}
