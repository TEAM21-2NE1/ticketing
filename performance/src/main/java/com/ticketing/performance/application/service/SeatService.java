package com.ticketing.performance.application.service;

import com.ticketing.performance.application.dto.seat.SeatInfoResponseDto;
import com.ticketing.performance.domain.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;


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
}
