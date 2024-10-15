package com.ticketing.order.application.dto.request;


import com.ticketing.order.application.dto.PrfRedisInfoDto;
import com.ticketing.order.application.dto.client.SeatInfoResponseDto;
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
