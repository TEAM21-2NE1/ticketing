package com.ticketing.review.application.client;

import com.ticketing.review.application.client.dto.OrderStatusDto;
import com.ticketing.review.application.client.dto.PerformanceInfoDto;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public interface ReviewClient {

  PerformanceInfoDto getPerformanceInfo(UUID performanceId);

  Map<Long, String> getUserNicknameList(List<Long> userIds);

  OrderStatusDto getOrderStatus(Long userId, UUID performanceId);
}
