package com.ticketing.review.domain.repository;

import com.ticketing.review.domain.model.ReviewLike;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewLikeRepository {

  // 좋아요 등록
  ReviewLike save(ReviewLike reviewLike);

  // 존재 여부 체크
  Optional<ReviewLike> findByUserIdAndReviewId(long userId, UUID reviewId);

}
