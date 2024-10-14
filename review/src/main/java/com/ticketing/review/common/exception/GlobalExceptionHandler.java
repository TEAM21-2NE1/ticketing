package com.ticketing.review.common.exception;

import com.ticketing.review.common.response.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<CommonResponse<?>> businessExceptionHandler(BusinessException e) {
    log.error(e.getMessage(), e);
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


  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<CommonResponse<?>> handleAccessDeniedException(AccessDeniedException e) {
    log.error(e.getMessage(), e);
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(CommonResponse.error("권한을 확인해주세요."));
  }

}