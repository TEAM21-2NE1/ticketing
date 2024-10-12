package com.ticketing.review.application.dto.response;

import com.ticketing.review.domain.model.Review;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateReviewResponseDto(
    UUID reviewId,
    UUID performanceId,
    long userId,
    String nickname,
    short rating,
    String title,
    String content,
    long likeCount,
    LocalDateTime createdAt
) implements Serializable {

  public static CreateReviewResponseDto fromEntity(Review review, String nickname) {
    return new CreateReviewResponseDto(
        review.getId(),
        review.getPerformanceId(),
        review.getUserId(),
        nickname,
        review.getRating(),
        review.getTitle(),
        review.getContent(),
        review.getLikeCount(),
        review.getCreatedAt()
    );
  }
}
