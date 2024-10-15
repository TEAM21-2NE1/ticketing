package com.ticketing.order.common.exception;

import com.ticketing.order.common.response.ExceptionMessage;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{

    private final ExceptionMessage errorCode;

    protected BusinessException(ExceptionMessage errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
