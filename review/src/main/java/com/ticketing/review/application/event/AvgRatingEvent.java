package com.ticketing.review.application.event;


import java.util.UUID;

public record AvgRatingEvent(
    UUID performanceId,
    short rating,
    short count
) {


  public static AvgRatingEvent toAvgRatingEvent(UUID performanceId, short beforeRating,
      short afterRating, RatingOperation operation) {

    short ratingSum = 0;
    ratingSum -= beforeRating;
    ratingSum += afterRating;

    return new AvgRatingEvent(performanceId, ratingSum, operation.getValue());
  }
}
