package com.ticketing.performance.common.exception;

import com.ticketing.performance.common.response.ErrorCode;

public class ForbiddenAccessException extends BusinessException{

    public ForbiddenAccessException(ErrorCode errorCode) {
        super(errorCode);
    }
}
