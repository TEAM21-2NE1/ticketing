package com.ticketing.review.application.dto.event;


import java.util.UUID;

public record ReviewDeleteErrorEvent(
    UUID performanceId,
    Long userId
) {

}
