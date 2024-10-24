package com.ticketing.order.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ticketing.order.application.dto.client.SeatInfoResponseDto;
import com.ticketing.order.domain.model.WaitingTicket;
import java.util.List;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record GetSeatsResponseDto(
        String status,  // "SUCCESS" 또는 "WAITING"
        Long waitingNumber,
        List<SeatInfoResponseDto> responseDtos
) {

    // 좌석 선택 성공
    public static GetSeatsResponseDto success(List<SeatInfoResponseDto> seatInfoResponseDtos) {
        return GetSeatsResponseDto.builder()
                .responseDtos(seatInfoResponseDtos)
                .status("SUCCESS")
                .build();
    }

    // 대기 상태 응답
    public static GetSeatsResponseDto waiting(WaitingTicket waitingTicket) {
        return GetSeatsResponseDto.builder()
                .status("WAITING")
                .waitingNumber(waitingTicket.getOrder())
                .build();
    }
}
