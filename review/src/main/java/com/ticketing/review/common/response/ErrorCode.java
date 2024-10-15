package com.ticketing.review.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "요청하신 리뷰를 찾을 수 없습니다."),
  REVIEW_INVALID_INPUT(HttpStatus.BAD_REQUEST, "유효하지 않은 입력값입니다."),
  ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 값입니다."),
  REVIEW_FORBIDDEN(HttpStatus.FORBIDDEN, "다른 유저가 등록한 리뷰를 수정 또는 삭제 할 수 없습니다."),
  EARLY_REVIEW(HttpStatus.BAD_REQUEST, "공연 시작 전에는 리뷰를 등록할 수 없습니다."),
  PERFORMANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 공연은 존재하지 않습니다."),
  REQUIRED_PERFORMANCE(HttpStatus.BAD_REQUEST, "공연 ID는 필수 값 입니다.");

  private final HttpStatus httpStatus;
  private final String message;
}