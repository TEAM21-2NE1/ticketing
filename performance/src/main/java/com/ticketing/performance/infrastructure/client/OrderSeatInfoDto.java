package com.ticketing.performance.infrastructure.client;


import com.ticketing.performance.application.dto.performance.PrfRedisInfoDto;
import com.ticketing.performance.application.dto.seat.SeatInfoResponseDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderSeatInfoDto {

    private PrfRedisInfoDto prfRedisInfoDto;
    private List<SeatInfoResponseDto> seatList;

}
