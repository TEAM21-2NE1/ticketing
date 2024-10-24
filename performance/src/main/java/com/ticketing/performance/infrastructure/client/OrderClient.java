package com.ticketing.performance.infrastructure.client;

import com.ticketing.performance.application.service.OrderService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order-service")
public interface OrderClient extends OrderService {

    @PostMapping(value = "/api/v1/orders/seats", consumes = "application/json")
    void insertSeats(@RequestBody OrderSeatInfoDto orderSeatInfoDto);
}
