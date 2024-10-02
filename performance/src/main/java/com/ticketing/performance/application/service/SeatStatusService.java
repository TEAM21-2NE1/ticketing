package com.ticketing.performance.application.service;

import com.ticketing.performance.application.dto.seat.CancelSeatResponseDto;
import com.ticketing.performance.application.dto.seat.ConfirmSeatResponseDto;
import com.ticketing.performance.presentation.dto.seat.CancelSeatRequestDto;
import com.ticketing.performance.presentation.dto.seat.ConfirmSeatRequestDto;

public interface SeatStatusService {


    ConfirmSeatResponseDto confirmSeat(ConfirmSeatRequestDto requestDto);

    CancelSeatResponseDto cancelSeat(CancelSeatRequestDto requestDto);
}
