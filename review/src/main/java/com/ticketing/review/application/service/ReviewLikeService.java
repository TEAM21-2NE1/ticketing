package com.ticketing.review.application.service;

import com.ticketing.review.application.dto.response.ReviewLikeResponseDto;
import com.ticketing.review.common.exception.ReviewException;
import com.ticketing.review.common.response.ErrorCode;
import com.ticketing.review.common.utils.SecurityUtils;
import com.ticketing.review.domain.model.Review;
import com.ticketing.review.domain.model.ReviewLike;
import com.ticketing.review.domain.repository.ReviewLikeRepository;
import com.ticketing.review.domain.repository.ReviewRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewLikeService {

  private final ReviewLikeRepository reviewLikeRepository;
  private final ReviewRepository reviewRepository;

  @Transactional(readOnly = false)
  public ReviewLikeResponseDto toggleReviewLike(UUID reviewId) {
    Optional<ReviewLike> reviewLike = reviewLikeRepository.findByUserIdAndReviewId(
        SecurityUtils.getUserId(),
        reviewId);

    if (reviewLike.isEmpty()) {
      Review review = reviewRepository.findById(reviewId).orElseThrow(
          () -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND)
      );
      ReviewLike savedReviewLike = reviewLikeRepository.save(
          ReviewLike.create(review, SecurityUtils.getUserId()));
      savedReviewLike.getReview().updateLikeCount(1);
      return ReviewLikeResponseDto.fromEntity(savedReviewLike);

    } else if (reviewLike.get().getIsDeleted().equals(true)) {
      reviewLike.get().addReviewLike(SecurityUtils.getUserId());
      reviewLike.get().getReview().updateLikeCount(1);
      return ReviewLikeResponseDto.fromEntity(reviewLike.get());

    } else {
      reviewLike.get().cancelReviewLike(SecurityUtils.getUserId());
      reviewLike.get().getReview().updateLikeCount(-1);
      return ReviewLikeResponseDto.fromEntity(reviewLike.get());
    }

  }
}
