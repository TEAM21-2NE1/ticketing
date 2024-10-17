package com.ticketing.payment.common.exception;

import com.ticketing.payment.common.response.ErrorCode;

public class IamportException extends BusinessException {


    public IamportException(ErrorCode errorCode) {
        super(errorCode);
    }
}
