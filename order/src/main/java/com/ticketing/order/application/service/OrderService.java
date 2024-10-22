package com.ticketing.order.application.service;

import com.ticketing.order.application.dto.request.CreateOrderRequestDto;
import com.ticketing.order.application.dto.response.CreateOrderResponseDto;

public interface OrderService {

    CreateOrderResponseDto createOrder(CreateOrderRequestDto requestDto, String userId,
            String userRole, String userEmail);

}
