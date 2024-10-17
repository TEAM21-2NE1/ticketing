package com.ticketing.performance.presentation.dto.seat;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderSeatRequestDto {

    private List<UUID> seatId;

}
