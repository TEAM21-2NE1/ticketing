package com.ticketing.review.common.exception;

import com.ticketing.review.common.response.ErrorCode;


public class ReviewException extends BusinessException {


  public ReviewException(ErrorCode errorCode) {
    super(errorCode);
  }
}