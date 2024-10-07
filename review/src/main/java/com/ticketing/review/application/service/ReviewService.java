package com.ticketing.review.application.service;

import com.ticketing.review.application.dto.request.CreateReviewRequestDto;
import com.ticketing.review.application.dto.request.UpdateReviewRequestDto;
import com.ticketing.review.application.dto.response.CreateReviewResponseDto;
import com.ticketing.review.application.dto.response.DeleteReviewResponseDto;
import com.ticketing.review.application.dto.response.ReviewListResponseDto;
import com.ticketing.review.application.dto.response.ReviewResponseDto;
import com.ticketing.review.application.dto.response.UpdateReviewResponseDto;
import com.ticketing.review.application.event.AvgRatingEvent;
import com.ticketing.review.application.event.RatingOperation;
import com.ticketing.review.common.exception.ReviewException;
import com.ticketing.review.common.response.ErrorCode;
import com.ticketing.review.domain.model.Review;
import com.ticketing.review.domain.repository.RedisRatingRepository;
import com.ticketing.review.domain.repository.ReviewRepository;
import com.ticketing.review.infrastructure.utils.SecurityUtils;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final RedisRatingRepository redisRatingRepository;

  /**
   * 리뷰 생성
   *
   * @param requestDto
   * @return
   */
  @Transactional(readOnly = false)
  public CreateReviewResponseDto createReview(CreateReviewRequestDto requestDto) {
    // 같은 공연에 대해 리뷰를 등록함
    if (reviewRepository.existsByUserIdAndPerformanceId(SecurityUtils.getUserId(),
        requestDto.performanceId())) {
      throw new ReviewException(ErrorCode.ALREADY_EXISTS);
    }

    // TODO order 서버에서 사용자가 해당 공연을 실제로 예매했던 게 맞는지 확인
    // TODO performance 서버에서 현재 시간이 등록하려는 공연의 시작 시간 이후 인지 확인
    // TODO 취소된 공연에 대한 리뷰 등록인지 확인
    // TODO 리뷰 제목 및 내용에 부적절한 언행이 포함되었는지 AI에게 문의

    // TODO User 서버에 userId에 대한 nickname 데이터 요청
    String nickname = "test";

    Review savedReview = reviewRepository.save(
        CreateReviewRequestDto.toEntity(requestDto, SecurityUtils.getUserId()));

    eventPublisher.publishEvent(
        AvgRatingEvent.toAvgRatingEvent(requestDto.performanceId(), (short) 0, requestDto.rating(),
            RatingOperation.CREATE));

    return CreateReviewResponseDto.fromEntity(savedReview, nickname);
  }


  /**
   * 리뷰 수정
   *
   * @param reviewId
   * @param requestDto
   * @return
   */
  @Transactional(readOnly = false)
  public UpdateReviewResponseDto updateReview(UUID reviewId, UpdateReviewRequestDto requestDto) {
    //DB에 reviewId에 대한 정보가 존재하는지 확인
    Review findReview = reviewRepository.findById(reviewId).orElseThrow(
        () -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND)
    );

    // 등록한 리뷰의 사용자와 일치하는지 확인
    // TODO 관리자의 경우 해당 로직을 타지 않도록 변경
    if (SecurityUtils.getUserId() != findReview.getUserId()) {
      throw new ReviewException(ErrorCode.REVIEW_FORBIDDEN);
    }

    // TODO 리뷰 제목 및 내용에 부적절한 언행이 포함되었는지 AI에게 문의
    // TODO User 서버에 userId에 대한 nickname 데이터 요청
    String nickname = "test";

    if (requestDto.rating() != null) {
      eventPublisher.publishEvent(
          AvgRatingEvent.toAvgRatingEvent(findReview.getPerformanceId(), findReview.getRating(),
              requestDto.rating(), RatingOperation.UPDATE));
    }

    findReview.updateReview(requestDto.rating(), requestDto.title(), requestDto.content());
    return UpdateReviewResponseDto.fromEntity(findReview, nickname);
  }


  /**
   * 리뷰 삭제
   *
   * @param reviewId
   * @return
   */
  @Transactional(readOnly = false)
  public DeleteReviewResponseDto deleteReview(UUID reviewId) {
    //DB에 reviewId에 대한 정보가 존재하는지 확인
    Review findReview = reviewRepository.findById(reviewId).orElseThrow(
        () -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND)
    );

    findReview.deleteReview(SecurityUtils.getUserId());

    eventPublisher.publishEvent(
        AvgRatingEvent.toAvgRatingEvent(findReview.getPerformanceId(), findReview.getRating(),
            (short) 0,
            RatingOperation.DELETE));

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


  @Transactional(readOnly = true)
  public ReviewListResponseDto getReviews(UUID performanceId, int page, int size, boolean isAsc,
      String sortBy,
      String title, String content) {
    Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

    // TODO User 서버에게 userId에 대한 nickname 데이터 요청 (bulk 방식으로 요청할 필요가 있음)
    String nickname = "test";
    Page<ReviewResponseDto> reviews = Page.empty();

    if (title == null && content == null) {

      reviews = reviewRepository.findAll(pageable)
          .map(review -> ReviewResponseDto.fromEntity(review, nickname));
    } else if (title == null) {
      reviews = reviewRepository.findByContentContaining(content, pageable)
          .map(review -> ReviewResponseDto.fromEntity(review, nickname));
    } else if (content == null) {
      reviews = reviewRepository.findByTitleContaining(title, pageable)
          .map(review -> ReviewResponseDto.fromEntity(review, nickname));
    } else {
      reviews = reviewRepository.findByTitleContainingAndContentContaining(title, content, pageable)
          .map(review -> ReviewResponseDto.fromEntity(review, nickname));
    }

    return ReviewListResponseDto.of(reviews, redisRatingRepository.getAvgRating(performanceId));


  }


}
