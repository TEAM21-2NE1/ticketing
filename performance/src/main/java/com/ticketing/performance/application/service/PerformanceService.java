package com.ticketing.performance.application.service;

import com.ticketing.performance.application.dto.hall.HallInfoResponseDto;
import com.ticketing.performance.application.dto.performance.CreatePrfResponseDto;
import com.ticketing.performance.application.dto.performance.PrfInfoResponseDto;
import com.ticketing.performance.application.dto.performance.PrfListResponseDto;
import com.ticketing.performance.application.dto.performance.UpdatePrfResponseDto;
import com.ticketing.performance.application.dto.seat.SeatInfoResponseDto;
import com.ticketing.performance.domain.model.Performance;
import com.ticketing.performance.domain.model.SeatStatus;
import com.ticketing.performance.domain.repository.PerformanceRepository;
import com.ticketing.performance.presentation.dto.performance.CreatePrfRequestDto;
import com.ticketing.performance.presentation.dto.performance.UpdatePrfRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final SeatService seatService;
    private final HallService hallService;
    private final ImageUploadService imageUploadService;

    @Transactional
    public CreatePrfResponseDto createPerformance(CreatePrfRequestDto requestDto) throws IOException {

        String posterUrl = imageUploadService.upload(requestDto.getImage());
        //todo: 유저정보 받기
        Long userId = 1L;
        Performance performance = Performance.create(requestDto, userId, posterUrl);

        performanceRepository.save(performance);

        return CreatePrfResponseDto.of(performance);
    }


    public Page<PrfListResponseDto> getPerformances(Pageable pageable) {
        return performanceRepository.findAll(pageable).map(PrfListResponseDto::of);
    }

    public PrfInfoResponseDto getPerformance(UUID performanceId) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new RuntimeException("error"));

        HallInfoResponseDto hall = hallService.getHall(performance.getHallId());


        List<SeatInfoResponseDto> seatList = seatService.getSeats(performanceId);
        int totalSeat = seatList.size();
        int availableSeat = seatList.stream()
                .filter(seat -> seat.getSeatStatus() == SeatStatus.AVAILABLE)
                .toList()
                .size();

        return PrfInfoResponseDto.of(performance, hall.getHallName(), totalSeat, availableSeat);
    }

    @Transactional
    public UpdatePrfResponseDto updatePerformance(UUID performanceId, UpdatePrfRequestDto requestDto) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new RuntimeException("error"));

        performance.update(requestDto);

        return UpdatePrfResponseDto.of(performance);

    }

    @Transactional
    public void deletePerformance(UUID performanceId) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new RuntimeException("error"));

        seatService.deleteSeatsByPerformanceId(performanceId);
        performance.delete(1L);
    }
}
