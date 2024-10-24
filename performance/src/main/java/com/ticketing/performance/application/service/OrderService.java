package com.ticketing.performance.application.service;

import com.ticketing.performance.infrastructure.client.OrderSeatInfoDto;

import java.util.UUID;

public interface OrderService  {

    void insertSeats(OrderSeatInfoDto orderSeatInfoDto);

    void deleteOrderSeats(UUID performanceId, String userId, String userRole, String email);

}
