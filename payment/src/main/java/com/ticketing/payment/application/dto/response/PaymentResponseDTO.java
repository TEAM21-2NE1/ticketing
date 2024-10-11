package com.ticketing.payment.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentResponseDTO {

    private String merchantUid;    // 주문번호

    private String cardNumber;     // 카드번호

    private String expiry;         // 유효기간

    private Integer cardQuota;         // 카드 할부개월수

    private String customerUid;    // 고객 주문번호

    private String name;           // 제품명

    private Double amount;         // 결제금액

    private String buyerName;      // 주문자명

    private String buyerEmail;     // 주문자 이메일
}
