package com.ticketing.review.domain.repository;

import com.ticketing.review.domain.model.Review;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID>, ReviewRepositoryCustom {

  // 존재 여부 체크
  boolean existsByUserIdAndPerformanceId(long userId, UUID performanceId);

  @EntityGraph(attributePaths = {"reviewLikes"})
  Optional<Review> findWithLikesById(UUID reviewId);

  @Modifying(clearAutomatically = true)
  @Query(value =
      "UPDATE p_review_like SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP, deleted_by = :userId "
          +
          "WHERE review_id IN (SELECT id FROM p_review WHERE performance_id = :performanceId); " +
          "UPDATE p_review SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP, deleted_by = :userId "
          +
          "WHERE performance_id = :performanceId", nativeQuery = true)
  void deleteByPerformanceId(@Param("performanceId") UUID performanceId,
      @Param("userId") Long userId);

  @Query(value = "SELECT AVG(r.rating) AS avgRating, COUNT(r.id) AS reviewCount FROM p_review r "
      + "WHERE r.performance_id = :performanceId AND r.is_deleted = false", nativeQuery = true)
  Map<String, Object> calculateAvgRatingAndCount(UUID performanceId);


  @Query(value =
      "SELECT r.performance_id AS performanceId, AVG(r.rating) AS avgRating, COUNT(r.id) AS reviewCount "
          +
          "FROM p_review r " +
          "WHERE r.is_deleted = false " +
          "GROUP BY r.performance_id", nativeQuery = true)
  List<Map<String, Object>> calculateAvgRatingAndCountBulk();
}
