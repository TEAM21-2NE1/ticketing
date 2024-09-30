package com.ticketing.performance.application.service;

import com.ticketing.performance.application.dto.performance.PrfListResponseDto;
import com.ticketing.performance.domain.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceRepository performanceRepository;


    public Page<PrfListResponseDto> getPerformances(Pageable pageable) {
        return performanceRepository.findAll(pageable).map(PrfListResponseDto::of);
    }
}
