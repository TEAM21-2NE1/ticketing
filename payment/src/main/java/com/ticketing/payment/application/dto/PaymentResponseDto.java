package com.ticketing.payment.application.dto;

import com.ticketing.payment.domain.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class PaymentResponseDto {

    private UUID paymentId;
    private String paymentUid;

    public static PaymentResponseDto of(Payment payment) {
        return PaymentResponseDto.builder()
                .paymentId(payment.getId())
                .paymentUid(payment.getPaymentUid())
                .build();
    }
}
