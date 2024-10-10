package com.ticketing.order.presentation.controller;


import static com.ticketing.order.common.response.SuccessMessage.CREATE_ORDER;
import static com.ticketing.order.common.response.SuccessResponse.success;

import com.ticketing.order.application.dto.request.CreateOrderRequestDto;
import com.ticketing.order.application.dto.response.CreateOrderResponseDto;
import com.ticketing.order.application.service.OrderService;
import com.ticketing.order.common.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    // 예매 생성
    @PostMapping
    public ResponseEntity<SuccessResponse<CreateOrderResponseDto>> createOrder(
            @RequestBody CreateOrderRequestDto requestDto,
            @RequestHeader(value = "X-User-Id") String userId) {

        return ResponseEntity.status(CREATE_ORDER.getHttpStatus())
                .body(success(CREATE_ORDER.getHttpStatus().value(),
                        CREATE_ORDER.getMessage(),
                        orderService.createOrder(requestDto, userId)));

    }


}
