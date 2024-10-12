package com.ticketing.performance.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    FORBIDDEN_ACCESS(HttpStatus.BAD_REQUEST, "해당 요청애 대한 권한이 없습니다."),

    // s3
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
    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 좌석을 찾을수 없습니다." ),


    SEAT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "좌석 정보가 이미 존재 합니다."),

    SEAT_ALREADY_HOLD(HttpStatus.CONFLICT,"다른 사용자가 선택한 좌석입니다." ),
    SEAT_ALREADY_BOOKED(HttpStatus.CONFLICT,"이미 예매 완료 된 좌석입니다." ),
    SEAT_QUERY_PERIOD_INVALID(HttpStatus.BAD_REQUEST, "좌석 조회 기간이 아닙니다." ),
    SEAT_SELECT_EXCEEDED(HttpStatus.BAD_REQUEST,"선택 가능 좌석수를 초과 했습니다." ),

    SEAT_STATUS_NOT_EXIST(HttpStatus.BAD_REQUEST, "존재 하지 않는 Seat Status 입니다."),
    TICKET_NOT_OPEN(HttpStatus.BAD_REQUEST,"티켓 오픈 시간이 아닙니다." ),
    SEAT_NOT_SELECTED_BY_USER(HttpStatus.BAD_REQUEST,"본인이 선택한 좌석이 아닙니다." );



    private final HttpStatus httpStatus;
    private final String message;
}
