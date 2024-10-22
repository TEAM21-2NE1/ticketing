package com.ticketing.review.application.scheduler;

import com.ticketing.review.domain.repository.RedisRatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AvgRatingScheduler {

  private final RedisRatingRepository redisRatingRepository;

  @Scheduled(fixedRate = 3600000, zone = "Asia/Seoul")
  @Caching(evict = {
      @CacheEvict(cacheNames = "reviewCache", allEntries = true, cacheManager = "reviewCacheManager"),
      @CacheEvict(cacheNames = {
          "reviewSearchCache"}, allEntries = true, cacheManager = "reviewCacheManager")
  })
  public void run() {
    redisRatingRepository.setAvgRatingBulk();

  }

}
