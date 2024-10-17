package com.ticketing.review.infrastructure.messaging.consumer;

import com.ticketing.review.application.service.ReviewService;
import com.ticketing.review.infrastructure.messaging.EventSerializer;
import com.ticketing.review.infrastructure.messaging.event.PerformanceCanceledEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewConsumer {

  private final ReviewService reviewService;

  @KafkaListener(topics = "performance-deleted", groupId = "review-group")
  public void handlePerformanceCanceled(String message) {
    PerformanceCanceledEvent event = EventSerializer.deserialize(message,
        PerformanceCanceledEvent.class);
    reviewService.deleteReviewByPerformance(event.getPerformanceId(), event.getUserId());
  }
}
