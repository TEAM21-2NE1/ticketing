package com.ticketing.payment.common.exception;


import com.ticketing.payment.common.response.ErrorCode;

public class ForbiddenAccessException extends BusinessException{

    public ForbiddenAccessException(ErrorCode errorCode) {
        super(errorCode);
    }
}
