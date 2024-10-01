package com.ticketing.performance.common.exception;

import com.ticketing.performance.common.response.ErrorCode;

public class SeatException extends BusinessException{


    public SeatException(ErrorCode errorCode) {
        super(errorCode);
    }
}
