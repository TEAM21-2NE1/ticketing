package com.ticketing.performance.presentation.controller;

import com.ticketing.performance.application.service.SeatService;
import com.ticketing.performance.common.response.CommonResponse;
import com.ticketing.performance.presentation.dto.seat.RegisterSeatPriceRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @PostMapping
    public ResponseEntity<CommonResponse<Void>> registerSeatPrice(@RequestBody RegisterSeatPriceRequestDto requestDto) {
        seatService.registerSeatPrice(requestDto);
        return ResponseEntity.ok(CommonResponse.success("update success"));
    }
}
