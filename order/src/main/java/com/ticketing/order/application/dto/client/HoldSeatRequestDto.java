package com.ticketing.order.application.dto.client;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class HoldSeatRequestDto {

    private UUID performanceId;
    private UUID seatId;
}
