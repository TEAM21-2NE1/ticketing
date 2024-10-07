package com.ticketing.review.application.event;


public enum RatingOperation {
  CREATE((short) 1),
  UPDATE((short) 0),
  DELETE((short) -1);

  private final short value;

  private RatingOperation(short value) {
    this.value = value;
  }

  public short getValue() {
    return value;
  }
}
