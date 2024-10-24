package com.ticketing.order.application.service;

import com.ticketing.order.application.dto.request.CreateOrderRequestDto;
import com.ticketing.order.application.dto.response.CreateOrderResponseDto;
import com.ticketing.order.domain.model.WaitingTicket;

public interface OrderService {

    CreateOrderResponseDto createOrder(CreateOrderRequestDto requestDto, String userId,
            String userRole, String userEmail);

    WaitingTicket getTicket(String userId);
}
