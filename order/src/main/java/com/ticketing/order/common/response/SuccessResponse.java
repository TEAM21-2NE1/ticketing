package com.ticketing.order.common.response;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.NonNull;

@Builder(access = PRIVATE)
public record SuccessResponse<T>(
        int resultCode, // resultCode 추가
        @NonNull
        String message,
        @JsonInclude(value = NON_NULL) T data
) implements CommonResponse {

    public static <T> SuccessResponse<T> success(int resultCode, String message, T data) {
        return SuccessResponse.<T>builder()
                .resultCode(resultCode) // resultCode 설정
                .message(message)
                .data(data)
                .build();
    }

    public static SuccessResponse<?> success(int resultCode, String message) {
        return SuccessResponse.builder()
                .resultCode(resultCode) // resultCode 설정
                .message(message)
                .build();
    }
}