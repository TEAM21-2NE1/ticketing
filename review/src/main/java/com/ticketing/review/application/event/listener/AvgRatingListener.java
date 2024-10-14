package com.ticketing.review.application.event.listener;


import com.ticketing.review.application.event.AvgRatingEvent;
import com.ticketing.review.domain.repository.RedisRatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AvgRatingListener {

  private final RedisRatingRepository redisRatingRepository;

  @EventListener
  @Async
  public void handleRatingAvgEvent(AvgRatingEvent event) {
    redisRatingRepository.saveAvgRating(event);
  }

}