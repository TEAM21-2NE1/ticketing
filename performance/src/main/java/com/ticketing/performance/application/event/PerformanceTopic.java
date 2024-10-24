package com.ticketing.performance.application.event;

public enum PerformanceTopic {
    DELETED("performance-deleted");

    private final String topic;

    PerformanceTopic(String topic) {
        this.topic = topic;
    }


    public String getTopic() {
        return topic;
    }
}
