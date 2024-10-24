package com.ticketing.order.presentation.controller;

import com.ticketing.order.application.dto.response.GetOrderListResponseDto;
import com.ticketing.order.application.dto.response.GetOrderPerformancesResponseDto;
import com.ticketing.order.application.dto.response.GetOrderResponseDto;
import com.ticketing.order.application.dto.response.GetOrderStatusResponse;
import com.ticketing.order.application.service.OrderRUDService;
import com.ticketing.order.application.service.OrderStatusService;
import com.ticketing.order.common.response.SuccessMessage;
import com.ticketing.order.common.response.SuccessResponse;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderRUDController {

    private final OrderStatusService orderStatusService;
    private final OrderRUDService orderRUDService;


    @GetMapping
    public ResponseEntity<?> getOrders() {
        List<GetOrderListResponseDto> responseDto = orderRUDService.getOrders();
        return ResponseEntity.ok(
                SuccessResponse.success(200, SuccessMessage.GET_ORDER.getMessage(), responseDto));
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable("orderId") UUID orderId) {
        GetOrderResponseDto responseDto = orderRUDService.getOrder(orderId);
        return ResponseEntity.ok(
                SuccessResponse.success(200, SuccessMessage.GET_ORDER.getMessage(), responseDto));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable("orderId") UUID orderId) {
        orderRUDService.deleteOrder(orderId);
        return ResponseEntity.ok(SuccessResponse.success("삭제 성공"));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<?> changeOrderBySuccess(@PathVariable("orderId") UUID orderId, @RequestBody UUID paymentId) {
        orderStatusService.changeOrderBySuccess(orderId, paymentId);
        return ResponseEntity.ok("성공");
    }

    @GetMapping("/status")
    public ResponseEntity<?> getOrderStatus(@RequestParam String userId,
            @RequestParam UUID performanceId) {
        GetOrderStatusResponse responseDto = orderRUDService.orderStatusResponse(userId,
                performanceId);
        return ResponseEntity.ok(
                SuccessResponse.success(200, SuccessMessage.GET_ORDER.getMessage(), responseDto));
    }

    // (자신의) 예매한 공연ID 목록 조회
    @GetMapping("/performances")
    public ResponseEntity<?> getOrderPerformanceIds() {
        GetOrderPerformancesResponseDto responseDto = orderRUDService.getOrderPerformanceIds();

        return ResponseEntity.ok(
                SuccessResponse.success(200, SuccessMessage.GET_ORDER.getMessage(), responseDto));
    }

    @GetMapping("/cancel/{orderId}")
    public ResponseEntity<?> cancelOrder(@PathVariable UUID orderId) {
        orderRUDService.cancelOrder(orderId);

        return ResponseEntity.ok(
                SuccessResponse.success(SuccessMessage.GET_ORDER.getMessage()));
    }
}
