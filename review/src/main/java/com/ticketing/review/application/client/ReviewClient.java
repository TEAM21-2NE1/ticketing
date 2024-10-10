package com.ticketing.review.application.client;

import com.ticketing.review.infrastructure.client.dto.GetNicknameResponseDto;
import com.ticketing.review.infrastructure.client.dto.PrfInfoResponseDto;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public interface ReviewClient {

  public PrfInfoResponseDto getPerformanceInfo(UUID performanceId);

  public List<GetNicknameResponseDto> getUserNicknameList(List<Long> userIds);

  // TODO Order 개발 후 구현 예정

}
