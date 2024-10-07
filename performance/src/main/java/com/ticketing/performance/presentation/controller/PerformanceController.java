package com.ticketing.performance.presentation.controller;

import com.ticketing.performance.application.dto.PageResponse;
import com.ticketing.performance.application.dto.performance.CreatePrfResponseDto;
import com.ticketing.performance.application.dto.performance.PrfInfoResponseDto;
import com.ticketing.performance.application.dto.performance.PrfListResponseDto;
import com.ticketing.performance.application.dto.performance.UpdatePrfResponseDto;
import com.ticketing.performance.application.service.PerformanceService;
import com.ticketing.performance.common.response.CommonResponse;
import com.ticketing.performance.presentation.dto.performance.PerformanceSearchRequestDto;
import com.ticketing.performance.presentation.dto.performance.CreatePrfRequestDto;
import com.ticketing.performance.presentation.dto.performance.UpdatePrfRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/performances")
@RequiredArgsConstructor
@Slf4j
public class PerformanceController {

    private final PerformanceService performanceService;

    @PreAuthorize("hasRole('P_MANAGER')")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<CommonResponse<CreatePrfResponseDto>> createPerformance(
            @Validated @ModelAttribute CreatePrfRequestDto requestDto){
        CreatePrfResponseDto responseDto = performanceService.createPerformance(requestDto);
        return ResponseEntity.ok(CommonResponse.success("create success!", responseDto));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<PrfListResponseDto>>> getPerformances(
            @Validated @ModelAttribute PerformanceSearchRequestDto requestDto)
    {
        Page<PrfListResponseDto> prfList = performanceService.getPerformances(requestDto);
        return ResponseEntity.ok(CommonResponse.success("get success!", PageResponse.of(prfList)));
    }

    @GetMapping("/{performanceId}")
    public ResponseEntity<CommonResponse<PrfInfoResponseDto>> getPerformance(@PathVariable UUID performanceId) {
        PrfInfoResponseDto responseDto = performanceService.getPerformance(performanceId);
        return ResponseEntity.ok(CommonResponse.success("get success!", responseDto));
    }

    @PreAuthorize("hasAnyRole('MANAGER','P_MANAGER')")
    @PatchMapping("/{performanceId}")
    public ResponseEntity<CommonResponse<UpdatePrfResponseDto>> updatePerformance(
            @PathVariable UUID performanceId,
            @Validated@RequestBody UpdatePrfRequestDto requestDto)
    {
        UpdatePrfResponseDto responseDto = performanceService.updatePerformance(performanceId, requestDto);
        return ResponseEntity.ok(CommonResponse.success("update success!", responseDto));
    }

    @PreAuthorize("hasAnyRole('MANAGER','P_MANAGER')")
    @DeleteMapping("/{performanceId}")
    public ResponseEntity<CommonResponse<Void>> deletePerformance(@PathVariable UUID performanceId) {
        performanceService.deletePerformance(performanceId);
        return ResponseEntity.ok(CommonResponse.success("delete success!"));
    }
}
