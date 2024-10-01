package com.ticketing.user.common.response;

import static lombok.AccessLevel.PRIVATE;

import lombok.Builder;
import lombok.NonNull;

@Builder(access = PRIVATE)
public record ExceptionResponse(

        @NonNull
        String message
) implements CommonResponse {

    public static ExceptionResponse of(String message) {
        return ExceptionResponse.builder()
                .message(message)
                .build();
    }
}