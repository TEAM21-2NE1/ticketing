package com.ticketing.review.domain.repository;


import com.ticketing.review.application.event.AvgRatingEvent;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisRatingRepository {

  void saveAvgRating(AvgRatingEvent avgRatingEvent);

  Double getAvgRating(UUID performanceId);
}
