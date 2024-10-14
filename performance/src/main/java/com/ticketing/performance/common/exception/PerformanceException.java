package com.ticketing.performance.common.exception;

import com.ticketing.performance.common.response.ErrorCode;

public class PerformanceException extends BusinessException{

    public PerformanceException(ErrorCode errorCode) {
        super(errorCode);
    }
}
