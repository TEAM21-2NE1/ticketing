package com.ticketing.review.application.dto.response;

import java.io.Serializable;
import org.springframework.data.domain.Page;

public record ReviewListResponseDto(
    Page<ReviewResponseDto> reviews,
    double ratingAvg
) implements Serializable {

  public static ReviewListResponseDto of(Page<ReviewResponseDto> reviews, double ratingAvg) {
    return new ReviewListResponseDto(reviews, ratingAvg);
  }
}
