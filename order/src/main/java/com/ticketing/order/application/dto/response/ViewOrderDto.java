package com.ticketing.order.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public final class ViewOrderDto {
    private UUID orderId;
    private String itemName;
    private Integer totalAmount;

    public static ViewOrderDto of(UUID orderId, List<GetOrderResponseDto.SeatDetail> seats, Integer price) {
        return ViewOrderDto.builder()
                .orderId(orderId)
                .itemName(
                        seats.size() == 1 ?
                                formatSeatName(seats.get(0)) :
                                formatSeatName(seats.get(0)) + " 외 " + (seats.size() - 1) + "개"
                )
                .totalAmount(price)
                .build();
    }


    private static String formatSeatName(GetOrderResponseDto.SeatDetail seat) {
        return seat.seatType() + "-" + seat.seatRow() + "-" + seat.seatNum();
    }


}
