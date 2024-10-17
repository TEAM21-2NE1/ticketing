package com.ticketing.performance.application.event;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PrfDeletedEvent {

    private UUID performanceId;
    private Long userId;

    public static PrfDeletedEvent create(UUID performanceId, Long userId) {
        return new PrfDeletedEvent(performanceId, userId);
    }
}
