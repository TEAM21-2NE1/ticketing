package com.ticketing.payment.application.dto;

import com.ticketing.payment.domain.model.Payment;
import com.ticketing.payment.domain.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class PaymentInfoResponseDto {

    private UUID paymentId;
    private String paymentUid;
    private Long price;
    private PaymentStatus status;

    public static PaymentInfoResponseDto of(Payment payment) {
        return PaymentInfoResponseDto.builder()
                .paymentId(payment.getId())
                .paymentUid(payment.getPaymentUid())
                .price(payment.getPrice())
                .status(payment.getStatus())
                .build();
    }

}
