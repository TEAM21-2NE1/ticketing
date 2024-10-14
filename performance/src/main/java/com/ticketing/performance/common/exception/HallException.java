package com.ticketing.performance.common.exception;

import com.ticketing.performance.common.response.ErrorCode;

public class HallException extends BusinessException{


    public HallException(ErrorCode errorCode) {
        super(errorCode);
    }
}
