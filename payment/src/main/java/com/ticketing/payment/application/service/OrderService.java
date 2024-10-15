package com.ticketing.payment.application.service;

import com.ticketing.payment.common.response.CommonResponse;
import com.ticketing.payment.infrastructure.dto.GetOrderResponseDto;

import java.util.UUID;

public interface OrderService {

    CommonResponse<GetOrderResponseDto> getOrder(String userId, String role, String email, UUID orderId);

    void deleteOrder(String userId, String role, String email, UUID orderId);

    void changeOrderBySuccess(String userId, String role, String email, UUID orderId);
}
