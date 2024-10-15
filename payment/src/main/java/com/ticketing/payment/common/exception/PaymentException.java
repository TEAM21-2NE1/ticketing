package com.ticketing.payment.common.exception;

import com.ticketing.payment.common.response.ErrorCode;

public class PaymentException extends BusinessException{

    public PaymentException(ErrorCode errorCode) {
        super(errorCode);
    }
}
