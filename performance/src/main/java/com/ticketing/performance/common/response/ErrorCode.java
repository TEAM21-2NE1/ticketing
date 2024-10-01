package com.ticketing.performance.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {


    ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재 하는 값 입니다."),
    IO_EXCEPTION_ON_IMAGE_UPLOAD(HttpStatus.BAD_REQUEST, "이미지 업로드 예외 발생"),

    EMPTY_FILE_EXCEPTION(HttpStatus.BAD_REQUEST,"이미지 파일이 없습니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "이미지 확장자가 올바르지 않습니다."),


    PUT_OBJECT_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR,"이미지 업로드 실패" ),


    // hall
    HALL_NOT_FOUND(HttpStatus.NOT_FOUND, "hall not found"),



    //performance
    PERFORMANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 공연을 찾을 수 없습니다."),

    //seat

    SEAT_TYPE_NOT_EXIST(HttpStatus.BAD_REQUEST, "seat type 이 존재 하지 않습니다."),


    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 좌석을 찾을수 없습니다." )


    ;



    private final HttpStatus httpStatus;
    private final String message;
}
