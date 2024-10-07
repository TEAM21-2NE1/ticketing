package com.ticketing.performance.presentation.controller;

import com.ticketing.performance.application.dto.seat.SeatInfoResponseDto;
import com.ticketing.performance.application.service.SeatService;
import com.ticketing.performance.common.response.CommonResponse;
import com.ticketing.performance.presentation.dto.seat.CreateSeatRequestDto;
import com.ticketing.performance.presentation.dto.seat.UpdateSeatPriceRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @PreAuthorize("hasAnyRole('MANAGER','P_MANAGER')")
    @PostMapping
    public ResponseEntity<CommonResponse<Void>> createSeat(@Validated @RequestBody CreateSeatRequestDto requestDto) {
        seatService.createSeat(requestDto);
        return ResponseEntity.ok(CommonResponse.success("create success!"));
    }

    @PreAuthorize("hasAnyRole('MANAGER','P_MANAGER')")
    @PatchMapping
    public ResponseEntity<CommonResponse<Void>> updateSeatPrice(@Validated @RequestBody UpdateSeatPriceRequestDto requestDto) {
        seatService.updateSeatPrice(requestDto);
        return ResponseEntity.ok(CommonResponse.success("update success"));
    }

    @GetMapping("/performances/{performanceId}")
    public ResponseEntity<CommonResponse<List<SeatInfoResponseDto>>> getSeats(@PathVariable UUID performanceId) {
        List<SeatInfoResponseDto> seats = seatService.getSeats(performanceId);
        return ResponseEntity.ok(CommonResponse.success("get success!", seats));
    }


}
