package com.ticketing.user.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."),
    USER_INVALID_INPUT(HttpStatus.BAD_REQUEST, "유효하지 않은 입력값입니다."),

    USER_ROLE_INVALID(HttpStatus.BAD_REQUEST, "사용자의 권한(role)이 올바르지 않습니다."),

    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 사용중인 이메일입니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 사용중인 닉네임입니다."),

    ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 값입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}