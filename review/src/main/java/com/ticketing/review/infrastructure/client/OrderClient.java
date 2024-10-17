package com.ticketing.review.infrastructure.client;

import com.ticketing.review.common.response.CommonResponse;
import com.ticketing.review.infrastructure.client.dto.GetOrderStatusResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient("order-service")
public interface OrderClient {

  @GetMapping("/api/v1/orders/status")
  ResponseEntity<CommonResponse<GetOrderStatusResponse>> getOrderStatus(
      @RequestHeader("X-User-Id") Long securityUserId,
      @RequestHeader("X-User-Role") String userRole, @RequestHeader("X-User-Email") String email,
      @RequestParam String userId, @RequestParam UUID performanceId);
}


