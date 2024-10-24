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
import com.ticketing.payment.common.util.SecurityUtil;
import com.ticketing.payment.domain.model.Payment;
import com.ticketing.payment.domain.repository.PaymentRepository;
import com.ticketing.payment.presentation.dto.CreatePaymentRequestDto;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final IamportClient iamportClient;
    private final OrderService orderService;


    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              @Value("${rest.api.key}") String restApiKey,
                              @Value("${rest.api.secret}") String restApiSecret,
                              OrderService orderService) {
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
        this.iamportClient = new IamportClient(restApiKey, restApiSecret);
    }

    @Transactional
    public PaymentResponseDto createPayment(CreatePaymentRequestDto requestDto) {

        UUID orderUid = null;
        long iamportPrice = 0L;
        String impUid = "";

        try {
            IamportResponse<com.siot.IamportRestClient.response.Payment> iamportResponse =
                    iamportClient.paymentByImpUid(requestDto.getPaymentUid());
            impUid = iamportResponse.getResponse().getImpUid();
            iamportPrice = iamportResponse.getResponse().getAmount().longValue();
            orderUid = UUID.fromString(requestDto.getOrderUid());

            Long price = orderService.getOrder(SecurityUtil.getId().toString(),
                    SecurityUtil.getRole(),
                    SecurityUtil.getEmail(),
                    orderUid).data().getTotalAmount().longValue();

            if (!"paid".equals(iamportResponse.getResponse().getStatus())) {
                throw new PaymentException(ErrorCode.PAYMENT_FAILED);
            }

            if (price != iamportPrice) {
                throw new PaymentException(ErrorCode.PAYMENT_AMOUNT_TAMPERED);
            }
            Payment payment = Payment.create(iamportPrice, impUid);

            paymentRepository.save(payment);

            // order 상태값 변경
            orderService.changeOrderBySuccess(SecurityUtil.getId().toString(),
                    SecurityUtil.getRole(),
                    SecurityUtil.getEmail(), orderUid);

            return PaymentResponseDto.of(payment);

        } catch (IamportResponseException | IOException e) {
            throw new IamportException(ErrorCode.IAMPORT_ERROR);
        } catch (FeignException | PaymentException e) {
            cancelAndDeleteOrder(orderUid, impUid, iamportPrice);
            throw e;
        } catch (IllegalArgumentException e) {
            cancelAndDeleteOrder(null, impUid, iamportPrice);
            throw new PaymentException(ErrorCode.INVALID_UUID);
        }
    }

    private void cancelAndDeleteOrder(UUID orderUid, String impUid, long iamportPrice) {
        if (impUid != null) {
            try {
                log.info("취소로직 실행");
                log.info("{}, {}", impUid, iamportPrice);
                iamportClient.cancelPaymentByImpUid(new CancelData(impUid, true, BigDecimal.valueOf(iamportPrice)));
            } catch (IamportResponseException | IOException ex) {
                log.error("결제 취소 중 오류 발생: {}", ex.getMessage(), ex);
            }
        }
        if (orderUid != null) {
            try {
                orderService.deleteOrder(
                        SecurityUtil.getId().toString(),
                        SecurityUtil.getRole(),
                        SecurityUtil.getEmail(),
                        orderUid
                );
            } catch (FeignException ex) {
                log.error("주문 삭제 중 오류 발생: {}", ex.getMessage(), ex);
            }
        }
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

        cancelImpPayment(paymentUid, price);

        payment.cancel();
    }

    private void cancelImpPayment(String paymentUid, Long price) {
        try {
            iamportClient.cancelPaymentByImpUid(new CancelData(paymentUid, true, BigDecimal.valueOf(price)));
        } catch (IamportResponseException | IOException ex) {
            log.error("결제 취소 중 오류 발생: {}", ex.getMessage(), ex);
        }
    }

    @Transactional
    public void deletePayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));

        payment.delete();
    }
}
