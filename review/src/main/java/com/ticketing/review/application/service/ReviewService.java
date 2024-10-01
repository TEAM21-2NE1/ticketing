package com.ticketing.review.application.service;

import com.ticketing.review.application.dto.request.CreateReviewRequestDto;
import com.ticketing.review.application.dto.request.UpdateReviewRequestDto;
import com.ticketing.review.application.dto.response.CreateReviewResponseDto;
import com.ticketing.review.application.dto.response.DeleteReviewResponseDto;
import com.ticketing.review.application.dto.response.ReviewResponseDto;
import com.ticketing.review.application.dto.response.UpdateReviewResponseDto;
import com.ticketing.review.common.exception.ReviewException;
import com.ticketing.review.common.response.ErrorCode;
import com.ticketing.review.domain.model.Review;
import com.ticketing.review.domain.repository.ReviewRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

  private final ReviewRepository reviewRepository;

  /**
   * 리뷰 생성
   *
   * @param requestDto
   * @param userId
   * @return
   */
  @Transactional(readOnly = false)
  public CreateReviewResponseDto createReview(CreateReviewRequestDto requestDto, long userId) {
    // 같은 공연에 대해 리뷰를 등록함
    if (reviewRepository.existsByUserIdAndPerformanceId(userId, requestDto.performanceId())) {
      throw new ReviewException(ErrorCode.ALREADY_EXISTS);
    }

    // TODO order 서버에서 사용자가 해당 공연을 실제로 예매했던 게 맞는지 확인
    // TODO performance 서버에서 현재 시간이 등록하려는 공연의 시작 시간 이후 인지 확인
    // TODO 취소된 공연에 대한 리뷰 등록인지 확인
    // TODO 리뷰 제목 및 내용에 부적절한 언행이 포함되었는지 AI에게 문의

    // TODO User 서버에 userId에 대한 nickname 데이터 요청
    String nickname = "test";

    Review savedReview = reviewRepository.save(CreateReviewRequestDto.toEntity(requestDto, userId));
    calculateRatingAvg(requestDto.performanceId());

    return CreateReviewResponseDto.fromEntity(savedReview, nickname);
  }


  /**
   * 리뷰 수정
   *
   * @param reviewId
   * @param requestDto
   * @param userId
   * @return
   */
  @Transactional(readOnly = false)
  public UpdateReviewResponseDto updateReview(UUID reviewId, UpdateReviewRequestDto requestDto,
      long userId) {
    //DB에 reviewId에 대한 정보가 존재하는지 확인
    Review findReview = reviewRepository.findById(reviewId).orElseThrow(
        () -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND)
    );

    // 등록한 리뷰의 사용자와 일치하는지 확인
    // TODO 관리자의 경우 해당 로직을 타지 않도록 변경
    if (userId != findReview.getUserId()) {
      throw new ReviewException(ErrorCode.REVIEW_FORBIDDEN);
    }

    // TODO 리뷰 제목 및 내용에 부적절한 언행이 포함되었는지 AI에게 문의
    // TODO User 서버에 userId에 대한 nickname 데이터 요청
    String nickname = "test";
    findReview.updateReview(requestDto.rating(), requestDto.title(), requestDto.content());
    calculateRatingAvg(findReview.getPerformanceId());

    return UpdateReviewResponseDto.fromEntity(findReview, nickname);
  }


  /**
   * 리뷰 삭제
   *
   * @param reviewId
   * @param userId
   * @return
   */
  @Transactional(readOnly = false)
  public DeleteReviewResponseDto deleteReview(UUID reviewId, long userId) {
    //DB에 reviewId에 대한 정보가 존재하는지 확인
    Review findReview = reviewRepository.findById(reviewId).orElseThrow(
        () -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND)
    );

    findReview.deleteReview(userId);
    calculateRatingAvg(findReview.getPerformanceId());
    return DeleteReviewResponseDto.fromEntity(findReview);
  }


  /**
   * 리뷰 단건 조회
   *
   * @param reviewId
   * @return
   */
  @Transactional(readOnly = true)
  public ReviewResponseDto getReview(UUID reviewId) {
    Review findReview = reviewRepository.findById(reviewId).orElseThrow(
        () -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND)
    );

    // TODO User 서버에 userId에 대한 nickname 데이터 요청
    String nickname = "test";
    return ReviewResponseDto.fromEntity(findReview, nickname);
  }


  /**
   * 평균 평점 계산
   *
   * @param performanceId
   */
  private double calculateRatingAvg(UUID performanceId) {
    // TODO 추후에 레디스 캐싱 예정
    List<Review> reviews = reviewRepository.findByPerformanceId(performanceId);
    double ratingAvg = 0.0;
    if (reviews.isEmpty()) {
      return 0.0;
    }
    return ratingAvg = reviews.stream()
        .mapToDouble(Review::getRating)
        .average()
        .orElse(0.0);
  }


}
