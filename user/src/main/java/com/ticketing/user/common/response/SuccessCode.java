package com.ticketing.user.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    LOGIN_SUCCESS(HttpStatus.OK, "로그인을 성공했습니다."),
    CREATE_SUCCESS(HttpStatus.CREATED, "회원가입을 성공했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}