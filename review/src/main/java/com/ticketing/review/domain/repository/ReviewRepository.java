package com.ticketing.review.domain.repository;

import com.ticketing.review.domain.model.Review;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository {

  // 저장
  Review save(Review review);

  // 리뷰 목록 조회
  List<Review> findByPerformanceId(UUID performanceId);

  // 존재 여부 체크
  boolean existsByUserIdAndPerformanceId(long userId, UUID performanceId);
}
