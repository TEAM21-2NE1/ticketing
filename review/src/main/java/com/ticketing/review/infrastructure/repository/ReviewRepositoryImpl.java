package com.ticketing.review.infrastructure.repository;

import com.ticketing.review.domain.model.Review;
import com.ticketing.review.domain.repository.ReviewRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepositoryImpl extends JpaRepository<Review, UUID>, ReviewRepository {

  boolean existsByUserIdAndPerformanceId(long userId, UUID performanceId);

  @EntityGraph(attributePaths = {"reviewLikes"})
  Optional<Review> findWithLikesById(UUID reviewId);


  Page<Review> findByTitleContainingAndContentContaining(String title, String content,
      Pageable pageable);

  Page<Review> findByTitleContaining(String title, Pageable pageable);

  Page<Review> findByContentContaining(String content, Pageable pageable);
}
