package com.ticketing.review.domain.event;


import java.util.UUID;

public record ReviewDeleteErrorEvent(
    UUID performanceId,
    Long userId
) {

}
