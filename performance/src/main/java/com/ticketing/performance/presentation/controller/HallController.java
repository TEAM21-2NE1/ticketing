package com.ticketing.performance.presentation.controller;

import com.ticketing.performance.application.dto.PageResponse;
import com.ticketing.performance.application.dto.hall.CreateHallResponseDto;
import com.ticketing.performance.application.dto.hall.HallInfoResponseDto;
import com.ticketing.performance.application.dto.hall.HallListResponseDto;
import com.ticketing.performance.application.dto.hall.UpdateHallResponseDto;
import com.ticketing.performance.application.service.HallService;
import com.ticketing.performance.common.response.CommonResponse;
import com.ticketing.performance.presentation.dto.hall.CreateHallRequestDto;
import com.ticketing.performance.presentation.dto.hall.HallSearchRequestDto;
import com.ticketing.performance.presentation.dto.hall.UpdateHallRequestDto;
import com.ticketing.performance.presentation.dto.performance.PerformanceSearchRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/halls")
@RequiredArgsConstructor
public class HallController {

    private final HallService hallService;

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    public ResponseEntity<CommonResponse<CreateHallResponseDto>> createHall(@Validated @RequestBody CreateHallRequestDto requestDto) {
        CreateHallResponseDto createHallResponseDto = hallService.createHall(requestDto);
        return ResponseEntity.ok(CommonResponse.success("create success!", createHallResponseDto));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<HallListResponseDto>>> getHalls(
            @Validated @ModelAttribute HallSearchRequestDto requestDto) {
        Page<HallListResponseDto> hallList = hallService.getHalls(requestDto.toPageable());
        return ResponseEntity.ok(CommonResponse.success("get success!", PageResponse.of(hallList)));
    }

    @GetMapping("/{hallId}")
    public ResponseEntity<CommonResponse<HallInfoResponseDto>> getHall(@PathVariable UUID hallId) {
        HallInfoResponseDto hallInfo = hallService.getHall(hallId);
        return ResponseEntity.ok(CommonResponse.success("get info success!", hallInfo));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("{hallId}")
    public ResponseEntity<CommonResponse<UpdateHallResponseDto>> updateHall(@PathVariable UUID hallId,
                                                                            @Validated @RequestBody UpdateHallRequestDto requestDto) {
        UpdateHallResponseDto updateHallResponseDto = hallService.updateHall(hallId, requestDto);
        return ResponseEntity.ok(CommonResponse.success("update success!", updateHallResponseDto));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("{hallId}")
    public ResponseEntity<CommonResponse<Void>> deleteHall(@PathVariable UUID hallId) {
        hallService.deleteHall(hallId);
        return ResponseEntity.ok(CommonResponse.success("delete success!"));
    }
}
