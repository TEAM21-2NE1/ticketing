package com.ticketing.order.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessage {
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "조회한 주문이 존재하지 않습니다."),
    CAPACITY_EXCEED(HttpStatus.BAD_REQUEST, "동시 접속자가 초과되었습니다."),
    SEAT_UNAVAILABLE(HttpStatus.BAD_REQUEST, "선택한 좌석이 이미 예약되었거나 사용할 수 없습니다."),
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "결제 처리 중 오류가 발생했습니다."),
    INVALID_ORDER_REQUEST(HttpStatus.BAD_REQUEST, "유효하지 않은 주문 요청입니다."),
    SEAT_RESERVATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "좌석 예약 처리 중 오류가 발생했습니다."),

    SEAT_TYPE_NOT_EXIST(HttpStatus.BAD_REQUEST, "seat type 이 존재 하지 않습니다."),
    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 좌석을 찾을수 없습니다." ),
    SEAT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "좌석 정보가 이미 존재 합니다."),
    SEAT_ALREADY_HOLD(HttpStatus.CONFLICT,"다른 사용자가 선택한 좌석입니다." ),
    SEAT_ALREADY_BOOKED(HttpStatus.CONFLICT,"이미 예매 완료 된 좌석입니다." ),
    SEAT_QUERY_PERIOD_INVALID(HttpStatus.BAD_REQUEST, "좌석 조회 기간이 아닙니다." ),
    SEAT_SELECT_EXCEEDED(HttpStatus.BAD_REQUEST,"선택 가능 좌석수를 초과 했습니다." ),
    SEAT_STATUS_NOT_EXIST(HttpStatus.BAD_REQUEST, "존재 하지 않는 Seat Status 입니다."),
    TICKET_NOT_OPEN(HttpStatus.BAD_REQUEST,"티켓 오픈 시간이 아닙니다." ),
    SEAT_NOT_SELECTED_BY_USER(HttpStatus.BAD_REQUEST,"본인이 선택한 좌석이 아닙니다." ),
    SEAT_LOCK_FAILED(HttpStatus.BAD_REQUEST, "락 획득 실패"),;


    private final HttpStatus httpStatus;
    private final String message;
}
