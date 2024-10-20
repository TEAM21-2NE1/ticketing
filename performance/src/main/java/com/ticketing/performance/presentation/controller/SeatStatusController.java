package com.ticketing.performance.presentation.controller;

import com.ticketing.performance.application.dto.seat.CancelSeatResponseDto;
import com.ticketing.performance.application.dto.seat.ConfirmSeatResponseDto;
import com.ticketing.performance.application.scheduler.SeatScheduler;
import com.ticketing.performance.application.service.SeatStatusService;
import com.ticketing.performance.common.response.CommonResponse;
import com.ticketing.performance.presentation.dto.seat.CancelSeatRequestDto;
import com.ticketing.performance.presentation.dto.seat.ConfirmSeatRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/seats")
public class SeatStatusController {

    private final SeatStatusService seatStatusService;
    private final SeatScheduler seatScheduler;

    @PutMapping("/confirm")
    public ResponseEntity<CommonResponse<ConfirmSeatResponseDto>> confirmSeat(@RequestBody ConfirmSeatRequestDto requestDto) {
        ConfirmSeatResponseDto responseDto = seatStatusService.confirmSeat(requestDto);
        return ResponseEntity.ok(CommonResponse.success("좌석 예매 확정 성공", responseDto));
    }

    @PutMapping("/cancel")
    public ResponseEntity<CommonResponse<CancelSeatResponseDto>> cancelSeat(@RequestBody CancelSeatRequestDto requestDto) {
        CancelSeatResponseDto responseDto = seatStatusService.cancelSeat(requestDto);
        return ResponseEntity.ok(CommonResponse.success("좌석 예매 취소 성공", responseDto));
    }

    @GetMapping("/scheduler")
    public ResponseEntity<CommonResponse<Void>> holdSeat() {
        seatScheduler.run();
        return ResponseEntity.ok(CommonResponse.success("좌석 업데이트 성공"));
    }


}
