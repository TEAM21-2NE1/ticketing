package com.ticketing.order.common.response;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ticketing.order.application.dto.client.SeatInfoResponseDto;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;

@Builder(access = PRIVATE)
public record ExceptionResponse(
        int resultCode,
        @NonNull
        String message,
        @JsonInclude(value = NON_NULL)
        List<SeatInfoResponseDto> seatList

) implements CommonResponse {

    public static ExceptionResponse of(String message, int resultCode) {
        return ExceptionResponse.builder()
                .resultCode(resultCode)
                .message(message)
                .build();
    }

    public static ExceptionResponse of(String message, int resultCode, List<SeatInfoResponseDto> seatList) {
        return ExceptionResponse.builder()
                .resultCode(resultCode)
                .message(message)
                .seatList(seatList)
                .build();
    }
}