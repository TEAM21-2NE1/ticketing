package com.ticketing.review.application.client;

import com.ticketing.review.application.client.dto.PerformanceInfoDto;
import com.ticketing.review.application.client.dto.UserNicknameInfoDto;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public interface ReviewClient {

  PerformanceInfoDto getPerformanceInfo(UUID performanceId);

  List<UserNicknameInfoDto> getUserNicknameList(List<Long> userIds);

  // TODO Order 개발 후 구현 예정

}
