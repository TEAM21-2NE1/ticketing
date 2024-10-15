package com.ticketing.payment.domain.model;

import com.ticketing.payment.common.auditor.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_payment")
@SQLRestriction("is_deleted = false")
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Long price;

    @Enumerated(value = EnumType.STRING)
    private PaymentStatus status;

    private String paymentUid; // 결제 고유 번호

    @Builder
    public Payment(Long price, PaymentStatus status, String paymentUid) {
        this.price = price;
        this.status = status;
        this.paymentUid = paymentUid;
    }

    public void cancel() {
        this.status = PaymentStatus.CANCEL;
    }

    public void delete() {
        super.delete();
    }

    public static Payment create(Long price, String paymentUid) {
        return Payment.builder()
                .price(price)
                .paymentUid(paymentUid)
                .status(PaymentStatus.OK)
                .build();
    }
}
