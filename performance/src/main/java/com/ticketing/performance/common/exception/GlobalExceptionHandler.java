package com.ticketing.performance.common.exception;

import com.ticketing.performance.application.dto.seat.SeatInfoResponseDto;
import com.ticketing.performance.common.response.CommonResponse;
import com.ticketing.performance.common.response.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<CommonResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(CommonResponse.error(ErrorCode.FORBIDDEN_ACCESS.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CommonResponse<Void>> businessExceptionHandler(BusinessException e) {
        log.error(e.getMessage(),e);
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(CommonResponse.error(e.getMessage()));
    }

    @ExceptionHandler(SeatException.class)
    public ResponseEntity<CommonResponse<List<SeatInfoResponseDto>>> seatExceptionHandler(SeatException e) {
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(CommonResponse.errorWithData(e.getMessage(),e.getSeats()));
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(e.getStatusCode())
                .body(CommonResponse.errors(FieldError.of(e.getBindingResult())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.error(e.getLocalizedMessage()));
    }
}
