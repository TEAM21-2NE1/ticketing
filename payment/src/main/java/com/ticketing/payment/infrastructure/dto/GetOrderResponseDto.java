package com.ticketing.payment.infrastructure.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetOrderResponseDto {

    private UUID orderId;
    private String userId;
    private UUID performanceId;
    private List<SeatDetail> seats;
    private Integer totalAmount;
    private String orderStatus;
    private String paymentMethod;


    public static record SeatDetail(
            UUID seatId,
            Integer seatNum,
            Integer seatRow,
            String seatType
    ) {

    }
}
