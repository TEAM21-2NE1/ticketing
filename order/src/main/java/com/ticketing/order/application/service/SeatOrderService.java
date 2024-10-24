package com.ticketing.order.application.service;


import com.ticketing.order.application.dto.PrfRedisInfoDto;
import com.ticketing.order.application.dto.client.SeatInfoResponseDto;
import com.ticketing.order.application.dto.response.GetSeatsResponseDto;
import java.util.List;
import java.util.UUID;

public interface SeatOrderService {

    void saveSeatsToRedis(PrfRedisInfoDto prfRedisInfoDto, List<SeatInfoResponseDto> seatList);

    void holdSeat(UUID performanceId, UUID seatId);

    List<SeatInfoResponseDto> getSeatsFromRedis(UUID performanceId);

    GetSeatsResponseDto getSeats(UUID performanceId);

    SeatInfoResponseDto getSeatFromRedis(UUID performanceId, UUID seatId);

    void confirm(List<UUID> seatIds, UUID performanceId);

    void cancel(List<UUID> seaIds, UUID performanceId);

    void releaseExpiredSeats(UUID performanceId, UUID seatId, SeatInfoResponseDto seat,
            Long userId);

    void deleteOrderSeats(UUID performanceId);

}
