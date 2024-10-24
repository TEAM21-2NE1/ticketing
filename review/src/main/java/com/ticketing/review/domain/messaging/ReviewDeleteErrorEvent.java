package com.ticketing.review.domain.messaging;


import java.util.UUID;

public record ReviewDeleteErrorEvent(
    UUID performanceId,
    Long userId
) {

}
