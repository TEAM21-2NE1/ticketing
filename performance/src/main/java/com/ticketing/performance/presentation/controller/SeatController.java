package com.ticketing.performance.presentation.controller;

import com.ticketing.performance.application.service.SeatService;
import com.ticketing.performance.common.response.CommonResponse;
import com.ticketing.performance.presentation.dto.seat.CreateSeatRequestDto;
import com.ticketing.performance.presentation.dto.seat.UpdateSeatPriceRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @PostMapping
    public ResponseEntity<CommonResponse<Void>> createSeat(@RequestBody CreateSeatRequestDto requestDto) {
        seatService.createSeat(requestDto);
        return ResponseEntity.ok(CommonResponse.success("create success!"));
    }

    @PatchMapping
    public ResponseEntity<CommonResponse<Void>> updateSeatPrice(@RequestBody UpdateSeatPriceRequestDto requestDto) {
        seatService.updateSeatPrice(requestDto);
        return ResponseEntity.ok(CommonResponse.success("update success"));
    }
}
