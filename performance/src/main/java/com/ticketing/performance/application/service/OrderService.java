package com.ticketing.performance.application.service;

import com.ticketing.performance.infrastructure.client.OrderSeatInfoDto;

public interface OrderService  {

    void insertSeats(String userId,
            String userRole,
            String email,
            OrderSeatInfoDto orderSeatInfoDto);

}
