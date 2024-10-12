package com.ticketing.order.common.exception;


import com.ticketing.order.common.response.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.ticketing.order.common.response.ExceptionResponse.of;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderException.class)
    public ResponseEntity<ExceptionResponse> handleCompanyException(OrderException e) {
        return ResponseEntity.status(e.getHttpStatus())
                .body(of(e.getMessage(), e.getHttpStatus().value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(of(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST.value()));
    }
}