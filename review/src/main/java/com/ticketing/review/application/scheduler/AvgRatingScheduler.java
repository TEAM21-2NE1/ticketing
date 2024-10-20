package com.ticketing.review.application.scheduler;

import com.ticketing.review.domain.repository.RedisRatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AvgRatingScheduler {

  private final RedisRatingRepository redisRatingRepository;

  @Scheduled(fixedRate = 3600000, zone = "Asia/Seoul")
  public void run() {
    redisRatingRepository.setAvgRatingBulk();

  }

}
