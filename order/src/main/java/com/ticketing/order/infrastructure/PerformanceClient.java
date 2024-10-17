package com.ticketing.order.infrastructure;

import com.ticketing.order.application.dto.client.CancelSeatRequestDto;
import com.ticketing.order.application.dto.client.CancelSeatResponseDto;
import com.ticketing.order.application.dto.client.ConfirmSeatRequestDto;
import com.ticketing.order.application.dto.client.ConfirmSeatResponseDto;
import com.ticketing.order.application.dto.client.HoldSeatRequestDto;
import com.ticketing.order.application.dto.client.PrfInfoResponseDto;
import com.ticketing.order.application.dto.client.SeatInfoResponseDto;
import com.ticketing.order.application.dto.request.OrderSeatRequestDto;
import com.ticketing.order.common.response.SuccessResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "performance-service")
public interface PerformanceClient {

    @GetMapping("/api/v1/performances/{performanceId}")
    SuccessResponse<PrfInfoResponseDto> getPerformance(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Email") String email,
            @PathVariable UUID performanceId);

    @PutMapping("/api/v1/seats/hold")
    SuccessResponse<Void> holdSeats(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Email") String email,
            @RequestBody HoldSeatRequestDto requestDto
    );

    @PutMapping("/api/v1/seats/confirm")
    SuccessResponse<ConfirmSeatResponseDto> confirmSeats(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Email") String email,
            @RequestBody ConfirmSeatRequestDto requestDto
    );

    @PutMapping("/api/v1/seats/cancel")
    SuccessResponse<CancelSeatResponseDto> cancelSeats(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Email") String email,
            @RequestBody CancelSeatRequestDto requestDto
    );

    @PostMapping("/api/v1/seats/order")
    SuccessResponse<List<SeatInfoResponseDto>> orderSeats(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Email") String email,
            @RequestBody OrderSeatRequestDto requestDto
    );

}