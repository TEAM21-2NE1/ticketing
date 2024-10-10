package com.ticketing.order.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessage {
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "조회한 주문이 존재하지 않습니다."),
    CAPACITY_EXCEED(HttpStatus.BAD_REQUEST, "동시 접속자가 초과되었습니다.")
    ,
    KEYWORD_EMPTY(HttpStatus.BAD_REQUEST, "키워드는 최소 1자 이상 작성해 주셔야 합니다."),
    KEYWORD_NOT_PROVIDED(HttpStatus.NOT_FOUND, "검색한 키워드가 존재하지 않습니다."),
    COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 업체가 존재하지 않습니다."),
    COMPANY_TYPE_NOT_MATCH(HttpStatus.BAD_REQUEST, "잘못된 업체 타입입니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다."),
    INSUFFICIENT_PRODUCT_QUANTITY(HttpStatus.BAD_REQUEST, "상품의 수량이 부족합니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
