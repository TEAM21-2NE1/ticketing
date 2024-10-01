package com.ticketing.performance.common.exception;

import com.ticketing.performance.common.response.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CommonResponse<Void>> businessExceptionHandler(BusinessException e) {
        log.error(e.getMessage(),e);
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(CommonResponse.error(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(e.getStatusCode())
                .body(CommonResponse.errors(FieldError.of(e.getBindingResult())));
    }
}
