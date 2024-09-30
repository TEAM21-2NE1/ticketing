package com.ticketing.user.common.exception;

import com.ticketing.user.common.response.ErrorCode;
import org.springframework.http.HttpStatus;

public class UserException extends RuntimeException {

    private final ErrorCode errorCode;

    public UserException(ErrorCode errorCode) {
        super("[User Exception] : " + errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }

    public String getMessage() {
        return errorCode.getMessage();
    }
}
