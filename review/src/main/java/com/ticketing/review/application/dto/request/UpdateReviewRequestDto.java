package com.ticketing.review.application.dto.request;

import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;

public record UpdateReviewRequestDto(
    @Range(min = 1, max = 5, message = "평점은 1에서 5 사이여야 합니다.")
    Short rating,

    @Size(max = 100, message = "리뷰 제목은 최대 100자까지 입력 가능합니다")
    String title,

    @Size(max = 500, message = "리뷰 내용은 최대 500자까지 입력 가능합니다")
    String content) {

}