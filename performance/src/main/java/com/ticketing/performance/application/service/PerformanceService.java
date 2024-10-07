package com.ticketing.performance.application.service;

import com.ticketing.performance.application.dto.hall.HallInfoResponseDto;
import com.ticketing.performance.application.dto.performance.CreatePrfResponseDto;
import com.ticketing.performance.application.dto.performance.PrfInfoResponseDto;
import com.ticketing.performance.application.dto.performance.PrfListResponseDto;
import com.ticketing.performance.application.dto.performance.UpdatePrfResponseDto;
import com.ticketing.performance.application.dto.seat.SeatInfoResponseDto;
import com.ticketing.performance.common.exception.ForbiddenAccessException;
import com.ticketing.performance.common.exception.PerformanceException;
import com.ticketing.performance.common.response.ErrorCode;
import com.ticketing.performance.common.util.SecurityUtil;
import com.ticketing.performance.domain.model.Performance;
import com.ticketing.performance.domain.model.SeatStatus;
import com.ticketing.performance.domain.repository.PerformanceRepository;
import com.ticketing.performance.presentation.dto.performance.CreatePrfRequestDto;
import com.ticketing.performance.presentation.dto.performance.UpdatePrfRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public CreatePrfResponseDto createPerformance(CreatePrfRequestDto requestDto) {

        String posterUrl = imageUploadService.upload(requestDto.getImage());
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Performance performance = Performance.create(requestDto, userId, posterUrl);

        performanceRepository.save(performance);

        return CreatePrfResponseDto.of(performance);
    }


    public Page<PrfListResponseDto> getPerformances(Pageable pageable) {
        return performanceRepository.findAll(pageable).map(PrfListResponseDto::of);
    }

    public PrfInfoResponseDto getPerformance(UUID performanceId) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new PerformanceException(ErrorCode.PERFORMANCE_NOT_FOUND));

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
                .orElseThrow(() -> new PerformanceException(ErrorCode.PERFORMANCE_NOT_FOUND));

        checkRole(performance.getManagerId());

        performance.update(requestDto);

        return UpdatePrfResponseDto.of(performance);

    }

    @Transactional
    public void deletePerformance(UUID performanceId) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new PerformanceException(ErrorCode.PERFORMANCE_NOT_FOUND));

        checkRole(performance.getManagerId());

        seatService.deleteSeatsByPerformanceId(performanceId);

        performance.delete();
    }


    private void checkRole(Long managerId) {
        Long userId = SecurityUtil.getId();
        String role = SecurityUtil.getRole();

        if (role.equals("ROLE_P_MANAGER")) {
            if (!userId.equals(managerId)) {
                throw new ForbiddenAccessException(ErrorCode.FORBIDDEN_ACCESS);
            }
        } else if (role.equals("ROLE_USER")) {
            throw new ForbiddenAccessException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }
}
