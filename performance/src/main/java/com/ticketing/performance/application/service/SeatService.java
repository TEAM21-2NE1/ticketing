package com.ticketing.performance.application.service;

import com.ticketing.performance.application.dto.hall.HallInfoResponseDto;
import com.ticketing.performance.application.dto.hall.HallSeatInfoResponseDto;
import com.ticketing.performance.application.dto.seat.SeatInfoResponseDto;
import com.ticketing.performance.domain.model.Seat;
import com.ticketing.performance.domain.repository.SeatRepository;
import com.ticketing.performance.presentation.dto.seat.CreateSeatRequestDto;
import com.ticketing.performance.presentation.dto.seat.UpdateSeatPriceRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final HallService hallService;


    public List<SeatInfoResponseDto> getSeats(UUID performanceId) {
        return seatRepository.findAllByPerformanceId(performanceId)
                .stream()
                .map(SeatInfoResponseDto::of)
                .toList();
    }


    @Transactional
    public void deleteSeatsByPerformanceId(UUID performanceId) {
        seatRepository.softDeleteSeatsByPerformanceId(performanceId);
    }

    @Transactional
    public void createSeat(CreateSeatRequestDto requestDto) {
        UUID performanceId = requestDto.getPerformanceId();

        HallInfoResponseDto hall = hallService.getHall(requestDto.getHallId());
        List<HallSeatInfoResponseDto> hallSeats = hall.getSeats();

        List<Seat> seats = new ArrayList<>();
        for (HallSeatInfoResponseDto hallSeat : hallSeats) {
            //todo : hall 좌석 구역과 요청 구역 검증
            Integer price = requestDto.getSections()
                    .stream()
                    .filter(s -> hallSeat.getSeatType().equals(s.getSeatType()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("error"))
                    .getPrice();


            for (int seatRows = 1; seatRows <= hallSeat.getRows(); seatRows++) {
                for (int seatNum = 1; seatNum < hallSeat.getSeatsPerRow(); seatNum++) {
                    Seat seat = Seat.create(performanceId, hallSeat.getSeatType(), seatRows, seatNum, price);
                    seats.add(seat);
                }
            }
        }

        seatRepository.saveAll(seats);
    }

    @Transactional
    public void updateSeatPrice(UpdateSeatPriceRequestDto requestDto) {
        seatRepository.updateSeatPriceBySeatType(
                requestDto.getSeatType(),
                requestDto.getPrice(),
                requestDto.getPerformanceId()
        );
    }
}
