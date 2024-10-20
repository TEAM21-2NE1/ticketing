package com.ticketing.payment.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    FORBIDDEN_ACCESS(HttpStatus.BAD_REQUEST, "해당 요청애 대한 권한이 없습니다."),
     PAYMENT_FAILED(HttpStatus.BAD_REQUEST,"결제 미완료"),
    PAYMENT_AMOUNT_TAMPERED(HttpStatus.BAD_REQUEST,"결제 금액 위변조 의심" ),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND,"결제 정보를 찾지 못 했습니다." ),
    PAYMENT_CANCEL_FAILED(HttpStatus.BAD_REQUEST,"결제 취소 실패"),
    IAMPORT_ERROR(HttpStatus.BAD_REQUEST,"포트원 에러"),

    ORDER_NOT_FOUND(HttpStatus.BAD_REQUEST,"존재 하지 않는 주문번호 입니다." ), INVALID_UUID(HttpStatus.BAD_REQUEST,"유효하지 않은 주문 UID 입니다 UUID 형식" );



    private final HttpStatus httpStatus;
    private final String message;
}
