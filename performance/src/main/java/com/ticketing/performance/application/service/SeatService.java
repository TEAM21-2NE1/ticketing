package com.ticketing.performance.application.service;

import com.ticketing.performance.application.dto.seat.SeatInfoResponseDto;
import com.ticketing.performance.presentation.dto.seat.CreateSeatRequestDto;
import com.ticketing.performance.presentation.dto.seat.OrderSeatRequestDto;
import com.ticketing.performance.presentation.dto.seat.UpdateSeatPriceRequestDto;

import java.util.List;
import java.util.UUID;

public interface SeatService {

    List<SeatInfoResponseDto> getSeats(UUID performanceId);

    List<SeatInfoResponseDto> getSeatsByManager(UUID performanceId);

    void deleteSeatsByPerformanceId(UUID performanceId);

    void rollbackSeatsByPerformanceId(UUID performanceId);

    void createSeat(CreateSeatRequestDto requestDto);

    void updateSeatPrice(UpdateSeatPriceRequestDto requestDto);

    List<SeatInfoResponseDto> getOrderSeats(OrderSeatRequestDto requestDto);
}
