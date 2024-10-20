package com.ticketing.review.domain.event;

public enum ReviewTopic {
  DELETE_ERROR("review-delete-error");

  private final String topic;

  ReviewTopic(String topic) {
    this.topic = topic;
  }

  public String getTopic() {
    return topic;
  }
}
