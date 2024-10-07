package com.ticketing.performance.common.exception;

import com.ticketing.performance.common.response.ErrorCode;

public class UnauthorizedAccessException extends BusinessException{

    public UnauthorizedAccessException(ErrorCode errorCode) {
        super(errorCode);
    }
}
