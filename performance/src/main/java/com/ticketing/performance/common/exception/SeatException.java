package com.ticketing.performance.common.exception;

import com.ticketing.performance.application.dto.seat.SeatInfoResponseDto;
import com.ticketing.performance.common.response.ErrorCode;
import lombok.Getter;

import java.util.List;

@Getter
public class SeatException extends BusinessException {

    private List<SeatInfoResponseDto> seats;

    public SeatException(ErrorCode errorCode) {
        super(errorCode);
    }

    public SeatException(ErrorCode errorCode, List<SeatInfoResponseDto> seats) {
        super(errorCode);
        this.seats = seats;
    }
}
