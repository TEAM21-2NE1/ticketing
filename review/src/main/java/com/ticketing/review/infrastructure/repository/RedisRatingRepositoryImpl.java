package com.ticketing.review.infrastructure.repository;

import com.ticketing.review.application.event.AvgRatingEvent;
import com.ticketing.review.domain.repository.RedisRatingRepository;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;


@RequiredArgsConstructor
@Repository
public class RedisRatingRepositoryImpl implements RedisRatingRepository {

  private final RedisTemplate<String, Object> redisTemplate;
  private final RedisScript<Boolean> saveAvgRatingScript;
  private final RedisScript<String> getAvgRatingScript;

  @Override
  public void saveAvgRating(AvgRatingEvent avgRatingEvent) {
    List<String> keys = Collections.emptyList();
    Object[] args = {avgRatingEvent.performanceId().toString(),
        String.valueOf(avgRatingEvent.count()),
        String.valueOf(avgRatingEvent.rating())};
    redisTemplate.execute(saveAvgRatingScript, keys, args);
  }

  @Override
  public Double getAvgRating(UUID performanceId) {
    List<String> keys = Collections.emptyList();
    Object[] args = {performanceId.toString()};
    return Double.parseDouble(redisTemplate.execute(getAvgRatingScript, keys, args));
  }
}
