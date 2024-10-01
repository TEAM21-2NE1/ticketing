package com.ticketing.performance.common.response;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.NonNull;

@Builder(access = PRIVATE)
public record CommonResponse<T> (
        @NonNull
        String message,
        @JsonInclude(value = NON_NULL) T data,
        @JsonInclude(value = NON_NULL) T errors
) {

    public static <T> CommonResponse<T> success(String message, T data) {
        return CommonResponse.<T>builder()
                .message(message)
                .data(data)
                .build();
    }

    public static CommonResponse<Void> success(String message) {
        return CommonResponse.<Void>builder().message(message).build();
    }

    public static CommonResponse<Void> error(String message) {
        return CommonResponse.<Void>builder().message(message).build();
    }

    public static <T> CommonResponse<T> errors(T errors) {
        return CommonResponse.<T>builder()
                .message("Validation failed")
                .errors(errors)
                .build();
    }
}