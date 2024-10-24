package com.ticketing.order.application.dto.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ticketing.order.config.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeatInfoResponseDto implements Serializable {

    private UUID seatId;
    private UUID performanceId;
    private UUID orderId;
    private String seatType;
    private Integer seatRow;
    private Integer seatNum;
    private Integer price;
    private SeatStatus seatStatus;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long userId;

    public void confirm(UUID orderId) {
        this.orderId = orderId;
        this.seatStatus = SeatStatus.BOOKED;

    }

    public void hold(Long userId) {
        this.userId = userId;
        this.seatStatus = SeatStatus.HOLD;
    }

    public void hold() {
        this.userId = SecurityUtil.getId();
        this.seatStatus = SeatStatus.HOLD;
    }

    public void cancel() {
        this.orderId = null;
        this.seatStatus = SeatStatus.AVAILABLE;
        this.userId = null;
    }
}
