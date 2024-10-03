package com.ticketing.performance.application.service;

import com.ticketing.performance.application.dto.seat.CancelSeatResponseDto;
import com.ticketing.performance.application.dto.seat.ConfirmSeatResponseDto;
import com.ticketing.performance.domain.model.Seat;
import com.ticketing.performance.domain.repository.SeatRepository;
import com.ticketing.performance.presentation.dto.seat.CancelSeatRequestDto;
import com.ticketing.performance.presentation.dto.seat.ConfirmSeatRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SeatStatusServiceImpl implements SeatStatusService{

    private final SeatRepository seatRepository;


    @Override
    public ConfirmSeatResponseDto confirmSeat(ConfirmSeatRequestDto requestDto) {

        List<Seat> seats = seatRepository.findAllByIds(requestDto.getSeatIds());

        seats.forEach(seat -> seat.confirm(requestDto.getOrderId()));

        return ConfirmSeatResponseDto.of(seats);
    }

    @Override
    public CancelSeatResponseDto cancelSeat(CancelSeatRequestDto requestDto) {

        List<Seat> seats = seatRepository.findAllByIds(requestDto.getSeatIds());

        seats.forEach(Seat::cancel);

        return CancelSeatResponseDto.of(seats);
    }
}
