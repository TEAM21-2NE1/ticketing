package com.ticketing.payment.application.service;


import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.ticketing.payment.application.dto.PaymentInfoResponseDto;
import com.ticketing.payment.application.dto.PaymentResponseDto;
import com.ticketing.payment.common.exception.IamportException;
import com.ticketing.payment.common.exception.PaymentException;
import com.ticketing.payment.common.response.ErrorCode;
import com.ticketing.payment.domain.model.Payment;
import com.ticketing.payment.domain.repository.PaymentRepository;
import com.ticketing.payment.presentation.dto.CreatePaymentRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;
    private final IamportClient iamportClient;
    private final OrderService orderService;


    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              @Value("${rest.api.key}")String restApiKey,
                              @Value("${rest.api.secret}")String restApiSecret,
                              OrderService orderService)
    {
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
        this.iamportClient = new IamportClient(restApiKey, restApiSecret);
    }

    @Transactional
    public PaymentResponseDto createPayment(CreatePaymentRequestDto requestDto){

        long iamportPrice;
        String impUid;

        try {
        IamportResponse<com.siot.IamportRestClient.response.Payment> iamportResponse =
                iamportClient.paymentByImpUid(requestDto.getPaymentUid());
        impUid = iamportResponse.getResponse().getImpUid();

        iamportPrice = iamportResponse.getResponse().getAmount().longValue();
        Long price = orderService.getOrder(requestDto.getOrderUid()).data().getPrice();
        if (!"paid".equals(iamportResponse.getResponse().getStatus())) {
            orderService.deleteOrder(requestDto.getOrderUid());
            throw new PaymentException(ErrorCode.PAYMENT_FAILED);
        }

        if (price != iamportPrice) {
            orderService.deleteOrder(requestDto.getOrderUid());
            iamportClient.cancelPaymentByImpUid(new CancelData(impUid, true, BigDecimal.valueOf(iamportPrice)));
            throw new PaymentException(ErrorCode.PAYMENT_AMOUNT_TAMPERED);
        }
        } catch (IamportResponseException | IOException e) {
            throw new IamportException(ErrorCode.IAMPORT_ERROR);
        }

        Payment payment = Payment.create(iamportPrice, impUid);

        paymentRepository.save(payment);

        // order 상태값 변경
        orderService.changeOrderBySuccess(requestDto.getOrderUid());

        return PaymentResponseDto.of(payment);
    }

    @Override
    public PaymentInfoResponseDto getPayment(UUID paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));

        return PaymentInfoResponseDto.of(payment);
    }

    @Transactional
    public void cancelPayment(UUID paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));

        Long price = payment.getPrice();
        String paymentUid = payment.getPaymentUid();

        try {
            iamportClient.cancelPaymentByImpUid(new CancelData(paymentUid, true, BigDecimal.valueOf(price)));
        } catch (IamportResponseException  | IOException e) {
            throw new PaymentException(ErrorCode.PAYMENT_FAILED);
        }

        payment.cancel();
    }

    @Transactional
    public void deletePayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));

        payment.delete();
    }
}
