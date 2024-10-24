package com.ticketing.order.infrastructure;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "payment-service")
public interface PaymentClient {


    @PutMapping("/api/v1/payments/{paymentId}")
    void cancelPayment(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Email") String email,
            @PathVariable("paymentId") UUID paymentId);

}
