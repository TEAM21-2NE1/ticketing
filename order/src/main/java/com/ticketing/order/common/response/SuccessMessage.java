package com.ticketing.order.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessMessage {
    CREATE_ORDER(HttpStatus.CREATED, "예매가 생성 되었습니다."),
    GET_ORDER(HttpStatus.OK, "예매 조회가 완료 되었습니다."),
    GET_ORDER_PERFORMANCE(HttpStatus.OK, "예매한 공연 목록 조회가 완료 되었습니다."),
    DELETE_ORDER(HttpStatus.OK, "예매 내역 삭제가 완료 되었습니다."),
    CANCEL_ORDER(HttpStatus.OK, "예매 취소가 완료 되었습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
