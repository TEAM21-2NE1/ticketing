package com.ticketing.order.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ticketing.order.domain.model.Order;
import com.ticketing.order.domain.model.WaitingTicket;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record CreateOrderResponseDto(
        UUID orderId,
        String userId,
        UUID performanceId,
        List<SeatDetail> seats,
        Integer totalAmount,
        String orderStatus,
        String paymentMethod,
        Long waitingNumber
) {

    public static record SeatDetail(
            UUID seatId,
            Integer seatNum,
            Integer seatRow,
            String seatType
    ) {

    }

    // 주문 성공
    public static CreateOrderResponseDto from(Order order,
            List<CreateOrderResponseDto.SeatDetail> seats) {
        return CreateOrderResponseDto.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .performanceId(order.getPerformanceId())
                .seats(seats)
                .totalAmount(order.getTotalAmount())
                .orderStatus(String.valueOf(order.getOrderStatus()))
                .paymentMethod(String.valueOf(order.getPaymentMethod()))
                .build();
    }

    // 대기 상태 응답
    public static CreateOrderResponseDto waiting(WaitingTicket waitingTicket) {
        return CreateOrderResponseDto.builder()
                .orderStatus("WAITING")
                .waitingNumber(waitingTicket.getOrder() + 1)
                .build();
    }
}