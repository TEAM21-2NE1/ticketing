package com.ticketing.review.domain.model;

import com.ticketing.review.common.auditor.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "p_review")
@Getter
@SQLRestriction("is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private UUID performanceId;

  @Column(nullable = false)
  private long userId;

  @Column(nullable = false)
  private short rating;

  @Column(nullable = false)
  private String title;

  private String content;

  @Column(nullable = false)
  private long likeCount;

  // TODO ReviewLike 생성 후 반영 예정

  @Builder
  private Review(UUID performanceId, long userId, short rating, String title, String content) {
    this.performanceId = performanceId;
    this.userId = userId;
    this.rating = rating;
    this.title = title;
    this.content = content;
  }


  public static Review create(UUID performanceId, long userId, short rating,
      String title,
      String content) {
    return Review.builder()
        .performanceId(performanceId)
        .userId(userId)
        .rating(rating)
        .title(title)
        .content(content)
        .build();
  }


  public void updateLikeCount(long count) {
    this.likeCount = count;
  }

  public void updateReview(Short rating, String title, String content) {
    if (rating != null && rating != 0) {
      this.rating = rating;
    }

    if (title != null) {
      this.title = title;
    }

    if (content != null) {
      this.content = content;
    }
  }

  //
  public void delete(long userId) {
    super.setDeleted(userId);
    // TODO reviewLike 에 대한 삭제 추가
  }
}
