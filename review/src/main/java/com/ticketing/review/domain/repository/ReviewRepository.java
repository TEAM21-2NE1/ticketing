package com.ticketing.review.domain.repository;

import com.ticketing.review.domain.model.Review;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository {

  // 저장
  Review save(Review review);

  // 리뷰 단건 조회
  Optional<Review> findById(UUID reviewId);

  Optional<Review> findWithLikesById(UUID reviewId);

  // 리뷰 목록 조회
  List<Review> findByPerformanceId(UUID performanceId);

  Page<Review> findByTitleContainingAndContentContaining(String title, String content,
      Pageable pageable);

  Page<Review> findByTitleContaining(String title, Pageable pageable);

  Page<Review> findByContentContaining(String content, Pageable pageable);

  Page<Review> findAll(Pageable pageable);


  // 리뷰 삭제
  void delete(Review review);

  // 존재 여부 체크
  boolean existsByUserIdAndPerformanceId(long userId, UUID performanceId);
}
