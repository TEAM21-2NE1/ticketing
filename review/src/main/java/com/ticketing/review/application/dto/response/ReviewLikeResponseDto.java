package com.ticketing.review.application.dto.response;

import com.ticketing.review.domain.model.ReviewLike;
import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewLikeResponseDto(
    UUID reviewId,
    long userId,
    LocalDateTime createdAt
) {

  public static ReviewLikeResponseDto fromEntity(ReviewLike reviewLike) {
    return new ReviewLikeResponseDto(
        reviewLike.getReview().getId(),
        reviewLike.getUserId(),
        reviewLike.getCreatedAt()
    );
  }
}
