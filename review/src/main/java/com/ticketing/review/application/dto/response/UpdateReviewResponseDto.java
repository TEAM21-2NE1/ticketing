package com.ticketing.review.application.dto.response;

import com.ticketing.review.domain.model.Review;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateReviewResponseDto(
    UUID reviewId,
    UUID performanceId,
    long userId,
    String nickname,
    short rating,
    String title,
    String content,
    long likeCount,
    LocalDateTime updatedAt
) implements Serializable {

  public static UpdateReviewResponseDto fromEntity(Review review, String nickname) {
    return new UpdateReviewResponseDto(
        review.getId(),
        review.getPerformanceId(),
        review.getUserId(),
        nickname,
        review.getRating(),
        review.getTitle(),
        review.getContent(),
        review.getLikeCount(),
        review.getUpdatedAt()
    );
  }
}
