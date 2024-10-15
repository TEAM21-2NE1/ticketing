package com.ticketing.order.common.exception;

import com.ticketing.order.application.dto.client.SeatInfoResponseDto;
import com.ticketing.order.common.response.ExceptionMessage;
import java.util.List;
import lombok.Getter;

@Getter
public class SeatException extends BusinessException {

    private List<SeatInfoResponseDto> seats;

    public SeatException(ExceptionMessage errorCode) {
        super(errorCode);
    }

    public SeatException(ExceptionMessage errorCode, List<SeatInfoResponseDto> seats) {
        super(errorCode);
        this.seats = seats;
    }
}
