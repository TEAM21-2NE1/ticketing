package com.ticketing.performance.presentation.controller;

import com.ticketing.performance.application.dto.rank.RankingResponseDto;
import com.ticketing.performance.application.service.RankService;
import com.ticketing.performance.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/performances")
public class RankController {

    private final RankService rankService;

    @GetMapping("/ranking")
    public ResponseEntity<CommonResponse<List<RankingResponseDto>>> getRank() {
        List<RankingResponseDto> rank = rankService.getRank();
        return ResponseEntity.ok(CommonResponse.success("get rank success!", rank));
    }
}
