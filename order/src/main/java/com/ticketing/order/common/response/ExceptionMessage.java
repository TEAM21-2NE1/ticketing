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
    SEAT_RESERVATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "좌석 예약 처리 중 오류가 발생했습니다.");


    private final HttpStatus httpStatus;
    private final String message;
}
