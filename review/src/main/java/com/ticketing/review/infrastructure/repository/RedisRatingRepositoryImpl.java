package com.ticketing.review.infrastructure.repository;

import com.ticketing.review.domain.repository.RedisRatingRepository;
import com.ticketing.review.domain.repository.ReviewRepository;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;


@RequiredArgsConstructor
@Repository
@Slf4j
public class RedisRatingRepositoryImpl implements RedisRatingRepository {

  private final RedisTemplate<String, Object> redisTemplate;
  private final RedisScript<Boolean> saveAvgRatingScript;
  private final ReviewRepository reviewRepository;

  @Override
  public Double getAvgRating(UUID performanceId) {

    String avgRatingKey = "avgRating:" + performanceId.toString();
    Map<Object, Object> ratingData = redisTemplate.opsForHash().entries(avgRatingKey);

    double avgRating = convertObjectToDouble(ratingData.get("avgRating"));
    long reviewCount = convertObjectToLong(ratingData.get("reviewCount"));

    if (avgRating == 0.0 || reviewCount < 50) {
      Map<String, Object> avgRatingAndCount = reviewRepository.calculateAvgRatingAndCount(
          performanceId);

      saveAvgRating(performanceId, avgRatingAndCount.get("avgRating"),
          avgRatingAndCount.get("reviewCount"));

      avgRating = convertObjectToDouble(avgRatingAndCount.get("avgRating"));
    }

    return avgRating;
  }

  @Override
  public void setAvgRatingBulk() {
    List<Map<String, Object>> result = reviewRepository.calculateAvgRatingAndCountBulk();

    int batchSize = 1000;
    int totalRecords = result.size();

    for (int i = 0; i < totalRecords; i += batchSize) {
      int start = i;
      redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
        int endSize = Math.min(start + batchSize, totalRecords);

        for (int j = start; j < endSize; j++) {
          Map<String, Object> data = result.get(j);
          UUID performanceId = (UUID) data.get("performanceId");
          double avgRating = convertObjectToDouble(data.get("avgRating"));
          long reviewCount = convertObjectToLong(data.get("reviewCount"));

          String avgRatingKey = "avgRating:" + performanceId.toString();
          
          redisTemplate.opsForHash().put(avgRatingKey, "avgRating", Double.toString(avgRating));
          redisTemplate.opsForHash().put(avgRatingKey, "reviewCount", Long.toString(reviewCount));
        }

        return null;
      });
    }
  }


  private double convertObjectToDouble(Object objectData) {
    if (objectData == null) {
      return 0.0;
    } else if (objectData instanceof String) {
      String stringData = objectData.toString();
      return Double.parseDouble(stringData);
    } else if (objectData instanceof BigDecimal bigDecimalData) {
      return bigDecimalData.doubleValue();
    } else if (objectData instanceof Double doubleData) {
      return doubleData;
    } else {
      return 0.0;
    }
  }


  private long convertObjectToLong(Object objectData) {
    if (objectData == null) {
      return 0;
    } else if (objectData instanceof String) {
      return Long.parseLong((String) objectData);
    } else if (objectData instanceof Long) {
      return (long) objectData;
    } else {
      return 0;
    }
  }


  public void saveAvgRating(UUID performanceId, Object avgRating, Object reviewCount) {
    List<String> keys = Collections.emptyList();
    Object[] args = {performanceId.toString(),
        String.valueOf(avgRating),
        String.valueOf(reviewCount)};
    redisTemplate.execute(saveAvgRatingScript, keys, args);
  }

}