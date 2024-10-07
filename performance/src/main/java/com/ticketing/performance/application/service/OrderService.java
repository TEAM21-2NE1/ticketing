package com.ticketing.performance.application.service;

import com.ticketing.performance.application.dto.seat.SeatInfoResponseDto;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface OrderService  {

    void seatUpdate(@RequestBody List<SeatInfoResponseDto> seatList);

}
