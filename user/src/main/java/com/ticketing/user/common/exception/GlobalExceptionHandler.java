package com.ticketing.user.common.exception;

import static com.ticketing.user.common.response.ExceptionResponse.of;

import com.ticketing.user.common.response.ExceptionResponse;
import java.time.format.DateTimeParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> validationExceptionHandler(MethodArgumentNotValidException ex) {

        // 유효성 검사 오류 메시지를 추출하여 첫 번째 오류 메시지 반환
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String errorMessage = (fieldError != null) ? fieldError.getDefaultMessage() : "Validation failed";

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(of(errorMessage));
    }

    // LocalDate 파싱 예외 처리
    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ExceptionResponse> dateTimeParseExceptionHandler(DateTimeParseException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(of("날짜의 형식이 올바르지 않습니다."));
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ExceptionResponse> userExceptionHandler(UserException e) {

        return ResponseEntity.status(e.getHttpStatus())
                .body(of(e.getMessage()));
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ExceptionResponse> exceptionHandler(Exception e) {
//
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(of(e.getLocalizedMessage()));
//    }
}
