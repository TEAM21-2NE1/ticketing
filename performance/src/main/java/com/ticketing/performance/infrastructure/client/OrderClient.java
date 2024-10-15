package com.ticketing.performance.infrastructure.client;

import com.ticketing.performance.application.service.OrderService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "order-service")
public interface OrderClient extends OrderService {

    // 임시로 만든 path
    @PostMapping(value = "/api/v1/orders/seats", consumes = "application/json")
    void insertSeats(@RequestHeader("X-User-Id")String userId,
            @RequestHeader("X-User-Role")String userRole,
            @RequestHeader("X-User-Email")String email,
            @RequestBody OrderSeatInfoDto orderSeatInfoDto);
}
