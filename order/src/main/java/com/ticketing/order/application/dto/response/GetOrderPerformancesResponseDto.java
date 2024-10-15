package com.ticketing.order.application.dto.response;

import java.util.List;
import lombok.Builder;
import java.util.UUID;

@Builder
public record GetOrderPerformancesResponseDto(
        List<UUID> performanceIds
) {

}
