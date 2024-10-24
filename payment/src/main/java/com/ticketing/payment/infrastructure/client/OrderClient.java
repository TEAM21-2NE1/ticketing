package com.ticketing.payment.infrastructure.client;

import com.ticketing.payment.application.service.OrderService;
import com.ticketing.payment.common.response.CommonResponse;
import com.ticketing.payment.infrastructure.dto.GetOrderResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient("order-service")
public interface OrderClient extends OrderService {

    @GetMapping("/api/v1/orders/{orderId}")
    CommonResponse<GetOrderResponseDto> getOrder(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Email") String email,
            @PathVariable("orderId") UUID orderId);

    @Override
    @DeleteMapping("/api/v1/orders/{orderId}")
    void deleteOrder(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Email") String email,
            @PathVariable("orderId") UUID orderId);

    @Override
    @PutMapping("/api/v1/orders/{orderId}")
    void changeOrderBySuccess(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Email") String email,
            @PathVariable("orderId") UUID orderId,
            @RequestBody UUID paymentId);
}
