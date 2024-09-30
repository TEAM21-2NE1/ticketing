package com.ticketing.review.common.response;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.NonNull;

@Builder(access = PRIVATE)
public record CommonResponse<T>(
    @NonNull
    String message,
    @JsonInclude(value = NON_NULL) T data
) {

  public static <T> CommonResponse<T> success(String message, T data) {
    return CommonResponse.<T>builder()
        .message(message)
        .data(data)
        .build();
  }

  public static CommonResponse<?> success(String message) {
    return CommonResponse.builder().message(message).build();
  }

  public static CommonResponse<?> error(String message) {
    return CommonResponse.builder().message(message).build();
  }
}