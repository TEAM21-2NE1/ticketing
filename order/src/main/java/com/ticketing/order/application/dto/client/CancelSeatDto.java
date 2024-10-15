package com.ticketing.order.application.dto.client;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CancelSeatDto {

    private UUID seatId;
    private SeatStatus seatStatus;

}
