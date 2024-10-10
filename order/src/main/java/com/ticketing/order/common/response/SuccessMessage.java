package com.ticketing.order.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessMessage {
    CREATE_ORDER(HttpStatus.CREATED, "예매가 완료되었습니다."),
    GET_ORDER(HttpStatus.OK, "주문 조회가 완료되었습니다."),
    UPDATE_ORDER(HttpStatus.OK, "주문 수정이 완료되었습니다."),
    DELETE_ORDER(HttpStatus.OK, "주문 삭제가 완료되었습니다."),
    SEARCH_ORDER(HttpStatus.OK, "주문 검색이 완료되었습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
