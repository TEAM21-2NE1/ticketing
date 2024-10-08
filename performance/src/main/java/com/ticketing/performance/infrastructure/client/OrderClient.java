package com.ticketing.performance.infrastructure.client;

import com.ticketing.performance.application.dto.seat.SeatInfoResponseDto;
import com.ticketing.performance.application.service.OrderService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "order-service")
public interface OrderClient extends OrderService {

    // 임시로 만든 path
    @PostMapping("/api/v1/orders/seat-update")
    void seatUpdate(@RequestBody List<SeatInfoResponseDto> seatList);
}
