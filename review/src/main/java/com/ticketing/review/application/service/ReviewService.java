package com.ticketing.review.application.service;

import com.ticketing.review.application.dto.request.CreateReviewRequestDto;
import com.ticketing.review.application.dto.response.CreateReviewResponseDto;
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