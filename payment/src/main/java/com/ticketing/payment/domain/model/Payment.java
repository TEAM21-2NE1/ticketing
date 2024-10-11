package com.ticketing.payment.domain.model;

import com.ticketing.payment.common.auditor.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
public class Payment extends BaseTimeEntity {

    @Id
    @UuidGenerator
    private UUID paymentId;

    private String customerUid;

    private String buyerName;      // 주문자명

    private String buyerEmail;     // 주문자 이메일

    private String productName;    // 제품명

    private Double amount;         // 결제 금액

    public void create(String customerUid,
                   String buyerName,
                   String buyerEmail,
                   String productName,
                   Double amount) {

        this.customerUid = customerUid;
        this.buyerName = buyerName;
        this.buyerEmail = buyerEmail;
        this.productName = productName;
        this.amount = amount;
    }
}
