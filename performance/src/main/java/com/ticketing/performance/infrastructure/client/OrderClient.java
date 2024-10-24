package com.ticketing.performance.infrastructure.client;

import com.ticketing.performance.application.service.OrderService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "order-service")
public interface OrderClient extends OrderService {

    @PostMapping(value = "/api/v1/orders/seats", consumes = "application/json")
    void insertSeats(@RequestBody OrderSeatInfoDto orderSeatInfoDto);

    @DeleteMapping("/api/v1/orders/seats/{performanceId}")
    void deleteOrderSeats(@PathVariable UUID performanceId,
                          @RequestHeader("X-User-Id") String userId,
                          @RequestHeader("X-User-Role") String userRole,
                          @RequestHeader("X-User-Email") String email);
}
