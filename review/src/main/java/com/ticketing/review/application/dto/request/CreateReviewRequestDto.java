package com.ticketing.review.application.dto.request;


import com.ticketing.review.domain.model.Review;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import org.hibernate.validator.constraints.Range;

public record CreateReviewRequestDto(
    @NotNull(message = "공연 ID는 필수 값입니다.")
    UUID performanceId,

    @NotNull(message = "평점은 필수 입력 값입니다.")
    @Range(min = 1, max = 5, message = "평점은 1에서 5 사이여야 합니다.")
    short rating,

    @NotBlank(message = "리뷰 제목은 필수 입력 값입니다.")
    @Size(max = 100, message = "리뷰 제목은 최대 100자까지 입력 가능합니다")
    String title,

    @Size(max = 500, message = "리뷰 내용은 최대 500자까지 입력 가능합니다")
    String content) {

  public static Review toEntity(CreateReviewRequestDto dto, long userId) {
    return Review.create(dto.performanceId(), userId, dto.rating(), dto.title(), dto.content());
  }
}