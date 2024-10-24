package com.ticketing.payment.presentation.controller;

import com.ticketing.payment.application.dto.PaymentInfoResponseDto;
import com.ticketing.payment.application.dto.PaymentResponseDto;
import com.ticketing.payment.application.service.PaymentService;
import com.ticketing.payment.common.response.CommonResponse;
import com.ticketing.payment.presentation.dto.CreatePaymentRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<CommonResponse<PaymentResponseDto>> createPayment(
            @RequestBody CreatePaymentRequestDto requestDto){

        PaymentResponseDto responseDto = paymentService.createPayment(requestDto);

        return ResponseEntity.ok(CommonResponse.success("결제 요청을 성공했습니다.", responseDto));
    }


    @GetMapping("/{paymentId}")
    public ResponseEntity<CommonResponse<PaymentInfoResponseDto>> getPayment(@PathVariable UUID paymentId) {
        PaymentInfoResponseDto payment = paymentService.getPayment(paymentId);
        return ResponseEntity.ok(CommonResponse.success("결제 상세 조회 성공", payment));
    }

    @PutMapping("/{paymentId}")
    public ResponseEntity<CommonResponse<Void>> cancelPayment(@PathVariable UUID paymentId) {
        paymentService.cancelPayment(paymentId);
        return ResponseEntity.ok(CommonResponse.success("결제 취소 성공"));
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<CommonResponse<Void>> deletePayment(@PathVariable UUID paymentId) {
        paymentService.deletePayment(paymentId);
        return ResponseEntity.ok(CommonResponse.success("결제 내역 삭제 성공"));
    }
}
