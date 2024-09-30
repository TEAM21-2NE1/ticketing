package com.ticketing.performance.presentation.controller;

import com.ticketing.performance.application.dto.performance.PrfInfoResponseDto;
import com.ticketing.performance.application.dto.performance.PrfListResponseDto;
import com.ticketing.performance.application.dto.performance.UpdatePrfResponseDto;
import com.ticketing.performance.application.service.PerformanceService;
import com.ticketing.performance.common.response.CommonResponse;
import com.ticketing.performance.presentation.dto.performance.UpdatePrfRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/performances")
@RequiredArgsConstructor
@Slf4j
public class PerformanceController {

    private final PerformanceService performanceService;


    @GetMapping
    public ResponseEntity<CommonResponse<Page<PrfListResponseDto>>> getPerformances(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PrfListResponseDto> prfList = performanceService.getPerformances(pageable);
        return ResponseEntity.ok(CommonResponse.success("get success!", prfList));
    }

    @GetMapping("/{performanceId}")
    public ResponseEntity<CommonResponse<PrfInfoResponseDto>> getPerformance(@PathVariable UUID performanceId) {
        PrfInfoResponseDto responseDto = performanceService.getPerformance(performanceId);
        return ResponseEntity.ok(CommonResponse.success("get success!", responseDto));
    }

    @PatchMapping("/{performanceId}")
    public ResponseEntity<CommonResponse<UpdatePrfResponseDto>> updatePerformance(@PathVariable UUID performanceId,
                                                                                  @RequestBody UpdatePrfRequestDto requestDto) {
        UpdatePrfResponseDto responseDto = performanceService.updatePerformance(performanceId, requestDto);
        return ResponseEntity.ok(CommonResponse.success("update success!", responseDto));
    }

    @DeleteMapping("/{performanceId}")
    public ResponseEntity<CommonResponse<Void>> deletePerformance(@PathVariable UUID performanceId) {
        performanceService.deletePerformance(performanceId);
        return ResponseEntity.ok(CommonResponse.success("delete success!"));
    }
}
