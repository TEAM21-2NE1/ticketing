package com.ticketing.performance.application.dto.seat;

import com.ticketing.performance.domain.model.Seat;
import com.ticketing.performance.domain.model.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class SeatInfoResponseDto {

    private UUID seatId;
    private UUID performanceId;
    private UUID orderId;
    private String seatType;
    private Integer seatRow;
    private Integer seatNum;
    private Integer price;
    private SeatStatus seatStatus;

    public static SeatInfoResponseDto of(Seat seat) {
        return SeatInfoResponseDto.builder()
                .seatId(seat.getId())
                .performanceId(seat.getPerformanceId())
                .orderId(seat.getOrderId())
                .seatType(seat.getSeatType())
                .seatRow(seat.getSeatRow())
                .seatNum(seat.getSeatNum())
                .price(seat.getPrice())
                .seatStatus(seat.getSeatStatus())
                .build();
    }
}
