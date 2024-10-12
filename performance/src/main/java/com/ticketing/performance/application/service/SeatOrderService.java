package com.ticketing.performance.application.service;

import com.ticketing.performance.application.dto.performance.PrfRedisInfoDto;
import com.ticketing.performance.application.dto.seat.SeatInfoResponseDto;

import java.util.List;
import java.util.UUID;

public interface SeatOrderService {

    void saveSeatsToRedis(PrfRedisInfoDto prfRedisInfoDto, List<SeatInfoResponseDto> seatList);

    void holdSeat(UUID performanceId, UUID seatId);

    List<SeatInfoResponseDto> getSeatsFromRedis(UUID performanceId);

    SeatInfoResponseDto getSeatFromRedis(UUID performanceId, UUID seatId);

    void confirm(List<UUID> seatIds, UUID performanceId);

    void cancel(List<UUID> seaIds, UUID performanceId);
}
