package com.ticketing.review.infrastructure.client;

import com.ticketing.review.application.client.ReviewClient;
import com.ticketing.review.infrastructure.client.dto.GetNicknameResponseDto;
import com.ticketing.review.infrastructure.client.dto.GetNicknamesRequestDto;
import com.ticketing.review.infrastructure.client.dto.PrfInfoResponseDto;
import com.ticketing.review.infrastructure.utils.SecurityUtils;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewClientImpl implements ReviewClient {

  private final OrderClient orderClient;
  private final PerformanceClient performanceClient;
  private final UserClient userClient;


  @Override
  public PrfInfoResponseDto getPerformanceInfo(UUID performanceId) {
    if (performanceId == null) {
      return null;
    }

    return performanceClient.getPerformance(SecurityUtils.getUserId(), SecurityUtils.getUserRole(),
        SecurityUtils.getUserEmail(), performanceId).getBody().data();
  }

  @Override
  public List<GetNicknameResponseDto> getUserNicknameList(List<Long> userIds) {
    if (userIds == null) {
      return null;
    }
    return userClient.nickname(GetNicknamesRequestDto.toGetNicknamesRequestDto(userIds)).getBody();
  }

  // TODO Order 개발 후 구현 예정
}
