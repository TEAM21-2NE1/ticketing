package com.ticketing.review.common.auditor;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  protected LocalDateTime createdAt;

  @CreatedBy
  @Column(name = "created_by", updatable = false)
  protected Long createdBy;

  @LastModifiedDate
  @Column(name = "updated_at")
  protected LocalDateTime updatedAt;

  @LastModifiedBy
  @Column(name = "updated_by")
  protected Long updatedBy;

  @Column(name = "deleted_at")
  protected LocalDateTime deletedAt;

  @Column(name = "deleted_by")
  protected Long deletedBy;

  @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
  protected Boolean isDeleted = false;  // JPA 필드 초기화와 DB 기본값 설정을 함께 사용

  protected void setDeleted(Long userId) {
    this.deletedAt = LocalDateTime.now();
    this.deletedBy = userId;
    this.isDeleted = true;
  }
}
