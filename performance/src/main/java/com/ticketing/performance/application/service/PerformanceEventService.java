package com.ticketing.performance.application.service;

import com.ticketing.performance.application.event.EventSerializer;
import com.ticketing.performance.application.event.PerformanceTopic;
import com.ticketing.performance.application.event.PrfDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PerformanceEventService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPerformanceDeletedEvent(PrfDeletedEvent event) {
        kafkaTemplate.send(PerformanceTopic.DELETED.getTopic(), EventSerializer.serialize(event));
    }
}
