package com.ticketing.review.domain.model;

import com.ticketing.review.common.auditor.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_review_like")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewLike extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private long userId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "review_id")
  private Review review;

  @Builder
  private ReviewLike(Review review, long userId) {
    this.review = review;
    this.userId = userId;
  }

  public static ReviewLike create(Review review, long userId) {
    return ReviewLike.builder()
        .review(review)
        .userId(userId)
        .build();
  }

  // 좋아요 취소
  public void cancelReviewLike(long userId) {
    super.setDeleted(userId);
  }

  // 좋아요 복원
  public void addReviewLike(long userId) {
    this.deletedAt = null;
    this.deletedBy = null;
    this.isDeleted = false;
  }

}
