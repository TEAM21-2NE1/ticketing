package com.ticketing.payment.presentation.controller;


import com.ticketing.payment.application.dto.request.PaymentRequestDTO;
import com.ticketing.payment.application.dto.response.PaymentResponseDTO;
import com.ticketing.payment.application.service.PaymentService;
import com.ticketing.payment.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequestMapping("/payment")
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

/*    @PostMapping
    public ResponseEntity<CommonResponse<Mono<PaymentResponseDTO>>> paymentCreate(@RequestBody PaymentRequestDTO paymentRequestDTO) {

        Mono<PaymentResponseDTO> paymentResponseDTO = paymentService.paymentCreate(userId, orderId, paymentRequestDTO);

        return ResponseEntity.ok().body(CommonResponse.success("결제 요청 성공", paymentResponseDTO));
    }*/

}