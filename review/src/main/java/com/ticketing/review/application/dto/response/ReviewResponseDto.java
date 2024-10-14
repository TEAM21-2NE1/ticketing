package com.ticketing.review.application.dto.response;

import com.ticketing.review.domain.model.Review;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewResponseDto(
    UUID reviewId,
    UUID performanceId,
    long userId,
    String nickname,
    short rating,
    String title,
    String content,
    long likeCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) implements Serializable {

  public static ReviewResponseDto fromEntity(Review review, String nickname) {
    return new ReviewResponseDto(
        review.getId(),
        review.getPerformanceId(),
        review.getUserId(),
        nickname,
        review.getRating(),
        review.getTitle(),
        review.getContent(),
        review.getLikeCount(),
        review.getCreatedAt(),
        review.getUpdatedAt()
    );
  }
}
