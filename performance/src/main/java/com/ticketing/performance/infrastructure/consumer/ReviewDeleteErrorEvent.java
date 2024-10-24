package com.ticketing.performance.infrastructure.consumer;

import java.util.UUID;

public record ReviewDeleteErrorEvent(
        UUID performanceId,
        Long userId
) {

}

