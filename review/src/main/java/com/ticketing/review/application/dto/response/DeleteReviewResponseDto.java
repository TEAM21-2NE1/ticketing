package com.ticketing.review.application.dto.response;

import com.ticketing.review.domain.model.Review;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record DeleteReviewResponseDto(
    UUID reviewId,
    LocalDateTime deletedAt
) implements Serializable {

  public static DeleteReviewResponseDto fromEntity(Review review) {
    return new DeleteReviewResponseDto(
        review.getId(),
        review.getDeletedAt()
    );
  }
}
