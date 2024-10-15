package com.ticketing.order.presentation.controller;


import static com.ticketing.order.common.response.SuccessMessage.CREATE_ORDER;
import static com.ticketing.order.common.response.SuccessResponse.success;

import com.ticketing.order.application.dto.client.HoldSeatRequestDto;
import com.ticketing.order.application.dto.client.SeatInfoResponseDto;
import com.ticketing.order.application.dto.request.CreateOrderRequestDto;
import com.ticketing.order.application.dto.response.CreateOrderResponseDto;
import com.ticketing.order.application.service.OrderService;
import com.ticketing.order.application.service.SeatOrderService;
import com.ticketing.order.common.response.SuccessResponse;
import java.util.List;
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
        log.info(userId, "controller");
        if ("WAITING".equals(responseDto.orderStatus())) {
            // 대기 상태인 경우
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(success(HttpStatus.ACCEPTED.value(),
                            "현재 대기열에 등록되었습니다.",
                            responseDto));
        } else {
            // 주문이 성공적으로 처리된 경우
            return ResponseEntity.status(CREATE_ORDER.getHttpStatus())
                    .body(success(CREATE_ORDER.getHttpStatus().value(),
                            CREATE_ORDER.getMessage(),
                            responseDto));
        }
    }

    // a. 좌석 선택
    @PutMapping("/hold")
    public ResponseEntity<SuccessResponse<?>> holdSeat(
            @RequestBody HoldSeatRequestDto requestDto) {
        seatOrderService.holdSeat(requestDto.getPerformanceId(), requestDto.getSeatId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(success("좌석 선택 성공"));
    }


    @GetMapping("/performances/{performanceId}")
    public ResponseEntity<SuccessResponse<List<SeatInfoResponseDto>>> getOrderSeats(
            @PathVariable UUID performanceId) {
        List<SeatInfoResponseDto> seats = seatOrderService.getSeatsFromRedis(performanceId);
        return ResponseEntity.ok(success(200, "좌석 조회 성공", seats));
    }

}
