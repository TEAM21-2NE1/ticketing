package com.ticketing.performance.application.service;

import com.ticketing.performance.application.dto.seat.SeatInfoResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SeatOrderService {

    void saveSeatsToRedis(UUID performanceId, LocalDateTime performanceTime, List<SeatInfoResponseDto> seatList, int ticketLimit);

    void holdSeat(UUID performanceId, UUID seatId);

    List<SeatInfoResponseDto> getSeatsFromRedis(UUID performanceId);

    SeatInfoResponseDto getSeatFromRedis(UUID performanceId, UUID seatId);

    void confirm(List<SeatInfoResponseDto> seats);

    void cancel(List<SeatInfoResponseDto> seats);
}
