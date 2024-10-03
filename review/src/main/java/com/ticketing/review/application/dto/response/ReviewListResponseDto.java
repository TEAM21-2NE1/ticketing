package com.ticketing.review.application.dto.response;

import org.springframework.data.domain.Page;

public record ReviewListResponseDto(
    Page<ReviewResponseDto> reviews,
    double ratingAvg
) {

  public static ReviewListResponseDto of(Page<ReviewResponseDto> reviews, double ratingAvg) {
    return new ReviewListResponseDto(reviews, ratingAvg);
  }
}
