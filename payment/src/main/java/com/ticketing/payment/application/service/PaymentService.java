package com.ticketing.payment.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketing.payment.application.dto.request.PaymentRequestDTO;
import com.ticketing.payment.application.dto.response.PaymentResponseDTO;
import com.ticketing.payment.domain.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    private final WebClient webClient;

    private final PaymentRepository paymentRepository;

    public PaymentService(WebClient.Builder webClientBuilder, PaymentRepository paymentRepository) {
        this.webClient = webClientBuilder.baseUrl("https://api.iamport.kr").build();
        this.paymentRepository = paymentRepository;
    }

    @Value("${imp.api.key}")
    private String apiKey;

    @Value("${imp.api.secret.key}")
    private String secretKey;

    @Value("${imp.api.uid}")
    private String merchantUid;

    // 수기 결제 처리
    @Transactional
    public Mono<PaymentResponseDTO> paymentCreate(Long userId,
                                                  UUID orderId,
                                                  PaymentRequestDTO paymentRequestDTO) {

        User user = userMapper.findByUserId(userId);
        Order order = orderMapper.findByOrderId(orderId);

        if (user == null) {
            return Mono.error(new RuntimeException("사용자를 찾을 수 없습니다."));
        }

        if (order == null) {
            return Mono.error(new RuntimeException("주문을 찾을 수 없습니다."));
        }

        PaymentRequestDTO fullRequest = new PaymentRequestDTO(
                paymentRequestDTO.getCardNumber(),
                paymentRequestDTO.getExpiry(),
                paymentRequestDTO.getCardQuota(),
                merchantUid,
                UUID.randomUUID().toString(), // customerUid
                order.getProductName(), // Order의 제품명
                order.getAmount(),      // Order의 결제 금액
                user.getName(),         // User의 이름
                user.getEmail()         // User의 이메일
        );

        return sendPaymentRequest(fullRequest).flatMap(paymentResponse -> {

            Payment payment = new Payment();
            payment.create(
                    fullRequest.getCustomerUid(),
                    fullRequest.getBuyerName(),
                    fullRequest.getBuyerEmail(),
                    fullRequest.getProductName(),
                    fullRequest.getAmount()
            );

            return Mono.fromCallable(() -> paymentRepository.save(payment))
                    .subscribeOn(Schedulers.boundedElastic())
                    .thenReturn(paymentResponse);
        });
    }

    // 결제 요청
    private Mono<PaymentResponseDTO> sendPaymentRequest(PaymentRequestDTO paymentRequestDTO) {
        return getAccessToken().flatMap(accessToken ->
                webClient.post()
                        .uri("/subscribe/payments/onetime")
                        .header("Authorization", "Bearer " + accessToken)
                        .bodyValue(paymentRequestDTO)
                        .retrieve()
                        .bodyToMono(String.class)
                        .map(this::parsePaymentResponse)
        );
    }

    // Access Token 요청
    private Mono<String> getAccessToken() {

        return webClient.post()
                .uri("/users/getToken")
                .bodyValue(Map.of("imp_key", apiKey, "imp_secret", secretKey))
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseToken);
    }

    private String parseToken(String response) {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.path("response").path("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("토큰 파싱 오류", e);
        }
    }

    private PaymentResponseDTO parsePaymentResponse(String response) {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(response).path("response");

            return new PaymentResponseDTO(
                    jsonNode.path("merchant_uid").asText(),
                    jsonNode.path("card_number").asText(),
                    jsonNode.path("expiry").asText(),
                    jsonNode.path("card_quota").asInt(),
                    jsonNode.path("customer_uid").asText(),
                    jsonNode.path("name").asText(),
                    jsonNode.path("amount").asDouble(),
                    jsonNode.path("buyer_name").asText(),
                    jsonNode.path("buyer_email").asText()
            );
        } catch (Exception e) {
            throw new RuntimeException("결제 응답 파싱 오류", e);
        }
    }
}