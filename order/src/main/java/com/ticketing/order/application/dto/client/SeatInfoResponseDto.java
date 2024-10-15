package com.ticketing.order.application.dto.client;

import com.ticketing.order.config.SecurityUtil;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SeatInfoResponseDto implements Serializable {

    private UUID seatId;
    private UUID performanceId;
    private UUID orderId;
    private String seatType;
    private Integer seatRow;
    private Integer seatNum;
    private Integer price;
    private SeatStatus seatStatus;
    private Long userId;

    public void confirm(UUID orderId) {
        this.orderId = orderId;
        this.seatStatus = SeatStatus.BOOKED;

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
