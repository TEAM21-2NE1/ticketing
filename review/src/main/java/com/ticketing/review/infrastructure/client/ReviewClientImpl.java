package com.ticketing.review.infrastructure.client;

import com.ticketing.review.application.client.ReviewClient;
import com.ticketing.review.application.client.dto.OrderStatusDto;
import com.ticketing.review.application.client.dto.PerformanceInfoDto;
import com.ticketing.review.common.exception.ReviewException;
import com.ticketing.review.common.response.CommonResponse;
import com.ticketing.review.common.response.ErrorCode;
import com.ticketing.review.infrastructure.client.dto.GetNicknamesRequestDto;
import com.ticketing.review.infrastructure.client.dto.GetOrderStatusResponse;
import com.ticketing.review.infrastructure.client.dto.PrfInfoResponseDto;
import com.ticketing.review.infrastructure.utils.SecurityUtils;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewClientImpl implements ReviewClient {

  private final OrderClient orderClient;
  private final PerformanceClient performanceClient;
  private final UserClient userClient;


  @Override
  public PerformanceInfoDto getPerformanceInfo(UUID performanceId) {
    if (performanceId == null) {
      return null;
    }

    ResponseEntity<CommonResponse<PrfInfoResponseDto>> performance = performanceClient.getPerformance(
        SecurityUtils.getUserId(), SecurityUtils.getUserRole(),
        SecurityUtils.getUserEmail(), performanceId);

    if (performance.getBody() == null || performance.getBody().data() == null) {
      throw new ReviewException(ErrorCode.PERFORMANCE_NOT_FOUND);
    }

    return performance.getBody().data().toPerformanceInfoDto();
  }

  @Override
  public Map<Long, String> getUserNicknameList(List<Long> userIds) {
    if (userIds == null) {
      return null;
    }

    ResponseEntity<Map<Long, String>> nicknames = userClient.nickname(
        GetNicknamesRequestDto.toGetNicknamesRequestDto(userIds));

    if (nicknames.getBody() == null) {
      throw new ReviewException(ErrorCode.USER_NOT_FUND);
    }

    return nicknames.getBody();
  }

  @Override
  public OrderStatusDto getOrderStatus(Long userId, UUID performanceId) {
    if (userId == null || performanceId == null) {
      return null;
    }

    ResponseEntity<CommonResponse<GetOrderStatusResponse>> orderStatus = orderClient.getOrderStatus(
        SecurityUtils.getUserId(), SecurityUtils.getUserRole(), SecurityUtils.getUserEmail(),
        String.valueOf(userId), performanceId);

    if (orderStatus.getBody() == null || orderStatus.getBody().data() == null) {
      throw new ReviewException(ErrorCode.ORDER_NOT_FOUND);
    }

    return orderStatus.getBody().data().toOrderStatusDto();
  }


}
