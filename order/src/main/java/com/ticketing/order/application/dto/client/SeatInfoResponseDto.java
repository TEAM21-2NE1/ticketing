package com.ticketing.order.application.dto.client;

import java.util.UUID;

public class SeatInfoResponseDto {

    private UUID seatId;
    private UUID performanceId;
    private UUID orderId;
    private String seatType;
    private Integer seatRow;
    private Integer seatNum;
    private Integer price;
    private SeatStatus seatStatus;
    private Long userId;
}