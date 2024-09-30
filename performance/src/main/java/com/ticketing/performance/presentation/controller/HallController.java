package com.ticketing.performance.presentation.controller;

import com.ticketing.performance.application.dto.hall.CreateHallResponseDto;
import com.ticketing.performance.application.dto.hall.HallInfoResponseDto;
import com.ticketing.performance.application.dto.hall.HallListResponseDto;
import com.ticketing.performance.application.dto.hall.UpdateHallResponseDto;
import com.ticketing.performance.application.service.HallService;
import com.ticketing.performance.common.response.CommonResponse;
import com.ticketing.performance.presentation.dto.hall.CreateHallRequestDto;
import com.ticketing.performance.presentation.dto.hall.UpdateHallRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/halls")
@RequiredArgsConstructor
public class HallController {

    private final HallService hallService;

    @PostMapping
    public ResponseEntity<CommonResponse<CreateHallResponseDto>> createHall(@RequestBody CreateHallRequestDto createHallRequestDto) {
        CreateHallResponseDto createHallResponseDto = hallService.createHall(createHallRequestDto);
        return ResponseEntity.ok(CommonResponse.success("create success!", createHallResponseDto));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<Page<HallListResponseDto>>> getHalls(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        //todo: page custome
        Page<HallListResponseDto> hallList = hallService.getHalls(pageable);
        return ResponseEntity.ok(CommonResponse.success("get success!", hallList));
    }

    @GetMapping("/{hallId}")
    public ResponseEntity<CommonResponse<HallInfoResponseDto>> getHall(@PathVariable UUID hallId) {
        HallInfoResponseDto hallInfo = hallService.getHall(hallId);
        return ResponseEntity.ok(CommonResponse.success("get info success!", hallInfo));
    }

    @PatchMapping("{hallId}")
    public ResponseEntity<CommonResponse<UpdateHallResponseDto>> updateHall(@PathVariable UUID hallId,
                                                                            @RequestBody UpdateHallRequestDto updateHallRequestDto) {
        UpdateHallResponseDto updateHallResponseDto = hallService.updateHall(hallId, updateHallRequestDto);
        return ResponseEntity.ok(CommonResponse.success("update success!", updateHallResponseDto));
    }

    @DeleteMapping("{hallId}")
    public ResponseEntity<CommonResponse<Void>> deleteHall(@PathVariable UUID hallId) {
        hallService.deleteHall(hallId);
        return ResponseEntity.ok(CommonResponse.success("delete success!"));
    }
}
