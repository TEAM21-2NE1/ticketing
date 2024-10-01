package com.ticketing.review.common.exception;

import com.ticketing.review.common.response.ErrorCode;
import lombok.Getter;

@Getter
public class ReviewException extends RuntimeException {

  private final String message;
  private final Integer statusCode;

  public ReviewException(ErrorCode errorCode) {
    this.message = errorCode.getMessage();
    this.statusCode = errorCode.getHttpStatus().value();
  }
}