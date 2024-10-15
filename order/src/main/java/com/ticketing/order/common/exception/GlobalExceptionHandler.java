package com.ticketing.order.common.exception;


import static com.ticketing.order.common.response.ExceptionResponse.of;

import com.ticketing.order.common.response.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionResponse> handleCompanyException(OrderException e) {
        return ResponseEntity.status(e.getHttpStatus())
                .body(of(e.getMessage(), e.getHttpStatus().value()));
    }


    @ExceptionHandler(SeatException.class)
    public ResponseEntity<ExceptionResponse> seatExceptionHandler(SeatException e) {
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(ExceptionResponse.of(e.getMessage(),e.getErrorCode().getHttpStatus().value(), e.getSeats()));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(of(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST.value()));
    }
}