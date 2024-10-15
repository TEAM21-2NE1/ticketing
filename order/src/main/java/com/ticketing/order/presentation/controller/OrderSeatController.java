package com.ticketing.order.presentation.controller;

import static com.ticketing.order.common.response.SuccessResponse.success;

import com.ticketing.order.application.dto.PrfRedisInfoDto;
import com.ticketing.order.application.dto.client.SeatInfoResponseDto;
import com.ticketing.order.application.dto.request.OrderSeatInfoDto;
import com.ticketing.order.application.service.SeatOrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@Slf4j
public class OrderSeatController {

    private final SeatOrderService seatOrderService;

    @PostMapping("/seats")
    public ResponseEntity<?> insertSeat(@RequestBody OrderSeatInfoDto requestDto) {
        log.info("{}", requestDto.getSeatList().size());

        PrfRedisInfoDto prfRedisInfoDto = requestDto.getPrfRedisInfoDto();
        List<SeatInfoResponseDto> seatList = requestDto.getSeatList();
        seatOrderService.saveSeatsToRedis(prfRedisInfoDto, seatList);
        return ResponseEntity.ok(success("성공"));

    }
}
