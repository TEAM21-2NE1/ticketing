package com.ticketing.order.application.dto.client;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CancelSeatRequestDto {

    private List<UUID> seatIds;
    private UUID orderId;
    private UUID performanceId;

}

