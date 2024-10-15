package com.ticketing.order.common.exception;

import com.ticketing.order.common.response.ExceptionMessage;
import org.springframework.http.HttpStatus;

public class OrderException extends RuntimeException{

    private final ExceptionMessage exceptionMessage;

    public OrderException(ExceptionMessage exceptionMessage) {
        super("[Product Exception] : " + exceptionMessage.getMessage());
        this.exceptionMessage = exceptionMessage;
    }

    public HttpStatus getHttpStatus() {
        return exceptionMessage.getHttpStatus();
    }

    public String getMessage() {
        return exceptionMessage.getMessage();
    }

}
