package com.ticketing.review.infrastructure.repository;

import com.ticketing.review.domain.model.ReviewLike;
import com.ticketing.review.domain.repository.ReviewLikeRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewLikeRepositoryImpl extends JpaRepository<ReviewLike, UUID>,
    ReviewLikeRepository {

  Optional<ReviewLike> findByUserIdAndReviewIdAndIsDeletedIsTrue(long userId, UUID reviewId);

}