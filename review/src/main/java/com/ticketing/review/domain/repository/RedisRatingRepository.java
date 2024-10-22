package com.ticketing.review.domain.repository;


import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisRatingRepository {

  Double getAvgRating(UUID performanceId);

  void setAvgRatingBulk();
}
