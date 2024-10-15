package com.ticketing.payment.application.service;

import com.ticketing.payment.common.response.CommonResponse;
import com.ticketing.payment.infrastructure.dto.GetOrderResponseDto;

import java.util.UUID;

public interface OrderService {

    CommonResponse<GetOrderResponseDto> getOrder(UUID orderId);

    void deleteOrder(UUID orderId);

    void changeOrderBySuccess(UUID orderId);
}
