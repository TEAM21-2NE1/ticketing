package com.ticketing.payment.application.dto.request;

import lombok.Getter;

@Getter
public class PaymentRequestDTO {

    private final String merchantUid;    // 상점 고유 번호
    private final String cardNumber;     // 카드 번호
    private final String expiry;         // 유효기간 (MM-YY)
    private final Integer cardQuota;     // 할부 개월수 (기본값: 0)
    private final String customerUid;    // 고객 주문번호
    private final String productName;           // 제품명
    private final Double amount;         // 결제금액
    private final String buyerName;      // 주문자명
    private final String buyerEmail;     // 주문자 이메일

    public PaymentRequestDTO(String cardNumber,
                             String expiry,
                             Integer cardQuota,
                             String merchantUid,
                             String customerUid,
                             String productName,
                             Double amount,
                             String buyerName,
                             String buyerEmail) {

        this.cardNumber = cardNumber;
        this.expiry = expiry;
        this.cardQuota = cardQuota != null ? cardQuota : 0; // 할부 개월수 기본값 0
        this.merchantUid = merchantUid;
        this.customerUid = customerUid;
        this.productName = productName;
        this.amount = amount;
        this.buyerName = buyerName;
        this.buyerEmail = buyerEmail;
    }
}