package com.ticketing.review.common.exception;

import com.ticketing.review.common.response.ErrorCode;
import lombok.Getter;


@Getter
public class BusinessException extends RuntimeException {

  private final ErrorCode errorCode;

  protected BusinessException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}