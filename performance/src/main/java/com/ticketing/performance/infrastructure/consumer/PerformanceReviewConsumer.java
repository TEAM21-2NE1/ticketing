package com.ticketing.performance.infrastructure.consumer;

import com.ticketing.performance.application.event.EventSerializer;
import com.ticketing.performance.application.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PerformanceReviewConsumer {

  private final PerformanceService performanceService;

  @KafkaListener(topics = "review-delete-error", groupId = "performance-group")
  public void handleReviewDeleteError(String message) {
    ReviewDeleteErrorEvent event = EventSerializer.deserialize(message, ReviewDeleteErrorEvent.class);
    performanceService.rollbackDeletePerformance(event.performanceId());
  }
}
