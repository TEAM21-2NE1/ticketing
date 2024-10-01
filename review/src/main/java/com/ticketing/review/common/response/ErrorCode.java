package com.ticketing.review.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 허브(물류센터)를 찾을 수 없습니다."),
  USER_INVALID_INPUT(HttpStatus.BAD_REQUEST, "유효하지 않은 입력값입니다."),

  ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 값입니다.");

  private final HttpStatus httpStatus;
  private final String message;
}