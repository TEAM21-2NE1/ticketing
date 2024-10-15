package com.ticketing.performance.application.service;

import com.ticketing.performance.application.dto.performance.CreatePrfResponseDto;
import com.ticketing.performance.application.dto.performance.PrfInfoResponseDto;
import com.ticketing.performance.application.dto.performance.PrfListResponseDto;
import com.ticketing.performance.application.dto.performance.UpdatePrfResponseDto;
import com.ticketing.performance.presentation.dto.performance.CreatePrfRequestDto;
import com.ticketing.performance.presentation.dto.performance.PerformanceSearchRequestDto;
import com.ticketing.performance.presentation.dto.performance.UpdatePrfRequestDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface PerformanceService {

    CreatePrfResponseDto createPerformance(CreatePrfRequestDto requestDto);

    Page<PrfListResponseDto> getPerformances(PerformanceSearchRequestDto requestDto);

    PrfInfoResponseDto getPerformance(UUID performanceId);

    UpdatePrfResponseDto updatePerformance(UUID performanceId, UpdatePrfRequestDto requestDto);

    void deletePerformance(UUID performanceId);
}
