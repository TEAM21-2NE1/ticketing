package com.ticketing.review.presentation.controller;

import com.ticketing.review.application.dto.request.CreateReviewRequestDto;
import com.ticketing.review.application.dto.request.UpdateReviewRequestDto;
import com.ticketing.review.application.dto.response.CreateReviewResponseDto;
import com.ticketing.review.application.dto.response.DeleteReviewResponseDto;
import com.ticketing.review.application.dto.response.ReviewLikeResponseDto;
import com.ticketing.review.application.dto.response.ReviewListResponseDto;
import com.ticketing.review.application.dto.response.ReviewResponseDto;
import com.ticketing.review.application.dto.response.UpdateReviewResponseDto;
import com.ticketing.review.application.service.ReviewLikeService;
import com.ticketing.review.application.service.ReviewService;
import com.ticketing.review.common.response.CommonResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

  private final ReviewService reviewService;
  private final ReviewLikeService reviewLikeService;

  /**
   * 리뷰 등록
   *
   * @param requestDto
   * @return
   */
  @PreAuthorize("hasRole('USER')")
  @PostMapping()
  public ResponseEntity<CommonResponse<CreateReviewResponseDto>> createReview(
      @RequestBody @Valid CreateReviewRequestDto requestDto) {
    return ResponseEntity.ok(CommonResponse.success("리뷰 등록에 성공하였습니다.",
        reviewService.createReview(requestDto)));
  }


  /**
   * 리뷰 수정
   *
   * @param reviewId
   * @param requestDto
   * @return
   */
  @PreAuthorize("hasAnyRole('USER','MANAGER')")
  @PatchMapping("/{reviewId}")
  public ResponseEntity<CommonResponse<UpdateReviewResponseDto>> updateReview(
      @PathVariable UUID reviewId, @RequestBody @Valid UpdateReviewRequestDto requestDto) {

    return ResponseEntity.ok(CommonResponse.success("리뷰 수정에 성공하였습니다.",
        reviewService.updateReview(reviewId, requestDto)));
  }


  /**
   * 리뷰 좋아요 관리
   *
   * @param reviewId
   * @return
   */
  @PreAuthorize("hasAnyRole('USER','MANAGER','P_MANAGER')")
  @PostMapping("/{reviewId}/like")
  public ResponseEntity<CommonResponse<ReviewLikeResponseDto>> toggleReviewLike(
      @PathVariable UUID reviewId) {
    return ResponseEntity.ok(CommonResponse.success("좋아요 처리에 성공하였습니다.",
        reviewLikeService.toggleReviewLike(reviewId)));
  }

  /**
   * 리뷰 삭제
   *
   * @param reviewId
   * @return
   */
  @PreAuthorize("hasAnyRole('USER','MANAGER')")
  @DeleteMapping("/{reviewId}")
  public ResponseEntity<CommonResponse<DeleteReviewResponseDto>> deleteReview(
      @PathVariable UUID reviewId) {

    return ResponseEntity.ok(CommonResponse.success("리뷰 삭제에 성공하였습니다.",
        reviewService.deleteReview(reviewId)));
  }


  /**
   * 리뷰 단건 조회
   *
   * @param reviewId
   * @return
   */
  @GetMapping("/{reviewId}")
  public ResponseEntity<CommonResponse<ReviewResponseDto>> getReview(
      @PathVariable UUID reviewId) {
    return ResponseEntity.ok(CommonResponse.success("리뷰 조회에 성공하였습니다.",
        reviewService.getReview(reviewId)));
  }


  /**
   * 리뷰 목록 조회
   *
   * @param performanceId
   * @param page
   * @param size
   * @param isAsc
   * @param sortBy
   * @param title
   * @param content
   * @return
   */
  @GetMapping()
  public ResponseEntity<CommonResponse<ReviewListResponseDto>> getReviews(
      @RequestParam(name = "performanceId", required = true) UUID performanceId,
      @RequestParam(name = "page", defaultValue = "1") int page,
      @RequestParam(name = "size", defaultValue = "10") int size,
      @RequestParam(name = "isAsc", defaultValue = "true") boolean isAsc,
      @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
      @RequestParam(name = "title", required = false) String title,
      @RequestParam(name = "content", required = false) String content) {
    return ResponseEntity.ok(CommonResponse.success("리뷰 목록 조회에 성공하였습니다.",
        reviewService.getReviews(performanceId, page - 1, size, isAsc, sortBy, title, content)));
  }


}
