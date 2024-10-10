package com.ticketing.order.common.response;

import static lombok.AccessLevel.PRIVATE;

import lombok.Builder;
import lombok.NonNull;

@Builder(access = PRIVATE)
public record ExceptionResponse(
        int resultCode,
        @NonNull
        String message
) implements CommonResponse {

    public static ExceptionResponse of(String message, int resultCode) {
        return ExceptionResponse.builder()
                .resultCode(resultCode)
                .message(message)
                .build();
    }
}