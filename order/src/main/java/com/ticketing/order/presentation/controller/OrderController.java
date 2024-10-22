package com.ticketing.order.presentation.controller;


import static com.ticketing.order.common.response.SuccessMessage.CREATE_ORDER;
import static com.ticketing.order.common.response.SuccessResponse.success;

import com.ticketing.order.application.dto.client.HoldSeatRequestDto;
import com.ticketing.order.application.dto.request.CreateOrderRequestDto;
import com.ticketing.order.application.dto.response.CreateOrderResponseDto;
import com.ticketing.order.application.dto.response.GetSeatsResponseDto;
import com.ticketing.order.application.service.OrderService;
import com.ticketing.order.application.service.SeatOrderService;
import com.ticketing.order.common.response.SuccessResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    private final SeatOrderService seatOrderService;

    // b. 예매 생성
    @PostMapping
    public ResponseEntity<SuccessResponse<CreateOrderResponseDto>> createOrder(
            @RequestBody CreateOrderRequestDto requestDto,
            @RequestHeader(value = "X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Email") String email
    ) {
        CreateOrderResponseDto responseDto = orderService.createOrder(requestDto, userId, userRole,
                email);

        return ResponseEntity.status(CREATE_ORDER.getHttpStatus())
                .body(success(CREATE_ORDER.getHttpStatus().value(),
                        CREATE_ORDER.getMessage(),
                        responseDto));
    }

    // a. 좌석 선택
    @PutMapping("/hold")
    public ResponseEntity<SuccessResponse<?>> holdSeat(
            @RequestBody HoldSeatRequestDto requestDto) {
        seatOrderService.holdSeat(requestDto.getPerformanceId(), requestDto.getSeatId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(success("좌석 상태 변경 성공"));
    }


    @GetMapping("/performances/{performanceId}")
    public ResponseEntity<SuccessResponse<GetSeatsResponseDto>> getOrderSeats(
            @PathVariable UUID performanceId) {
        GetSeatsResponseDto responseDto = seatOrderService.getSeats(performanceId);

        if ("WAITING".equals(responseDto.status())) {
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(success(HttpStatus.ACCEPTED.value(),
                            "대기 번호",
                            responseDto));
        } else {
            return ResponseEntity.ok(success(HttpStatus.OK.value(),
                    "좌석 조회 완료",
                    responseDto));
        }
    }

    @GetMapping("/rank")
    public ResponseEntity<SuccessResponse<?>> rank(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(SuccessResponse.success(HttpStatus.OK.value(),
                "대기 번호 조회 성공",
                orderService.getTicket(userId)));
    }


}
