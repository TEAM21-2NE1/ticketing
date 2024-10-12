package com.ticketing.review.infrastructure.client;

import com.ticketing.review.common.response.CommonResponse;
import com.ticketing.review.infrastructure.client.dto.PrfInfoResponseDto;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient("performance-service")
public interface PerformanceClient {

  @GetMapping("/api/v1/performances/{performanceId}")
  ResponseEntity<CommonResponse<PrfInfoResponseDto>> getPerformance(
      @RequestHeader("X-User-Id") Long userId, @RequestHeader("X-User-Role") String userRole,
      @RequestHeader("X-User-Email") String email, @PathVariable UUID performanceId);
}
