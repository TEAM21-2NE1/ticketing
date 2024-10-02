package com.ticketing.performance.common.exception;

import com.ticketing.performance.common.response.ErrorCode;
import lombok.Getter;

@Getter
public class S3Exception extends RuntimeException {

    private final String message;
    private final Integer statusCode;

    public S3Exception(ErrorCode errorCode) {
        this.message = errorCode.getMessage();
        this.statusCode = errorCode.getHttpStatus().value();
    }
}
