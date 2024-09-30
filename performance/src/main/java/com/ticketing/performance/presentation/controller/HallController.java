package com.ticketing.performance.presentation.controller;

import com.ticketing.performance.application.dto.CreateHallResponseDto;
import com.ticketing.performance.application.service.HallService;
import com.ticketing.performance.common.response.CommonResponse;
import com.ticketing.performance.presentation.dto.CreateHallRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
