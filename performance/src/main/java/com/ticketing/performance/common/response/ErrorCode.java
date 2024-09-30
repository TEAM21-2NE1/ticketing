package com.ticketing.performance.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    PERFORMANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 공연을 찾을 수 없습니다."),
    ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재 하는 값 입니다.");

    ;

    private final HttpStatus httpStatus;
    private final String message;
}
