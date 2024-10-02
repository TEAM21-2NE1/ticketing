package com.ticketing.performance.presentation.dto.seat;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CancelSeatRequestDto {

    private List<UUID> seatIds;
    private UUID orderId;

}
