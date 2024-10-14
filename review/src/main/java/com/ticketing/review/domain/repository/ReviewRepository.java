package com.ticketing.review.domain.repository;

import com.ticketing.review.domain.model.Review;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID>, ReviewRepositoryCustom {

  // 존재 여부 체크
  boolean existsByUserIdAndPerformanceId(long userId, UUID performanceId);

  @EntityGraph(attributePaths = {"reviewLikes"})
  Optional<Review> findWithLikesById(UUID reviewId);
}
