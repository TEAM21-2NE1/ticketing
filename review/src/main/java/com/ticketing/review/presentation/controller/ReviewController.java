package com.ticketing.review.presentation.controller;

import com.ticketing.review.application.dto.request.CreateReviewRequestDto;
import com.ticketing.review.application.dto.response.CreateReviewResponseDto;
import com.ticketing.review.application.service.ReviewService;
import com.ticketing.review.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

  private final ReviewService reviewService;
  // TODO 추후에 userId는 Filter로 분리할 예정

  /**
   * 리뷰 등록
   *
   * @param requestDto
   * @param userId
   * @return
   */
  @PostMapping()
  public ResponseEntity<CommonResponse<CreateReviewResponseDto>> createReview(
      @RequestBody CreateReviewRequestDto requestDto, @RequestHeader("X-User-Id") long userId) {
    return ResponseEntity.ok(CommonResponse.success("리뷰 등록에 성공하였습니다.",
        reviewService.createReview(requestDto, userId)));
  }


}
