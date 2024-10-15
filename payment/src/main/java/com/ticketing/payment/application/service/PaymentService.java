package com.ticketing.payment.application.service;

import com.ticketing.payment.application.dto.PaymentInfoResponseDto;
import com.ticketing.payment.application.dto.PaymentResponseDto;
import com.ticketing.payment.presentation.dto.CreatePaymentRequestDto;

import java.util.UUID;

public interface PaymentService {

    PaymentResponseDto createPayment(CreatePaymentRequestDto requestDto);

    PaymentInfoResponseDto getPayment(UUID paymentId);

    void cancelPayment(UUID paymentId);

    void deletePayment(UUID paymentId);
}

