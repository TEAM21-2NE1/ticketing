package com.ticketing.order.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrfRedisInfoDto {

    private UUID performanceId;
    private LocalDateTime performanceTime;
    private LocalDateTime ticketOpenTime;
    private int ticketLimit;

}