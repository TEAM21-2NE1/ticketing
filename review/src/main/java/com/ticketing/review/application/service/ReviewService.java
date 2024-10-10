package com.ticketing.review.application.service;

import com.ticketing.review.application.client.ReviewClient;
import com.ticketing.review.application.client.dto.PerformanceInfoDto;
import com.ticketing.review.application.client.dto.UserNicknameInfoDto;
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
import feign.FeignException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
  private final ReviewClient reviewClient;

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

    try {
      PerformanceInfoDto prfInfo = PerformanceInfoDto.toPerformanceInfoDto(
          reviewClient.getPerformanceInfo(requestDto.performanceId()));
      if (LocalDateTime.now().isBefore(prfInfo.performanceTime())) {
        throw new ReviewException(ErrorCode.EARLY_REVIEW);
      }
    } catch (FeignException.NotFound e) {
      throw new ReviewException(ErrorCode.PERFORMANCE_NOT_FOUND);
    }

    // TODO 리뷰 제목 및 내용에 부적절한 언행이 포함되었는지 AI에게 문의

    List<Long> userIds = new ArrayList<>();

    userIds.add(SecurityUtils.getUserId());
    String nickname = reviewClient.getUserNicknameList(userIds).get(0).nickname();

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
    if (SecurityUtils.getUserRole().equals("ROLE_USER")
        && SecurityUtils.getUserId() != findReview.getUserId()) {
      throw new ReviewException(ErrorCode.REVIEW_FORBIDDEN);
    }

    // TODO 리뷰 제목 및 내용에 부적절한 언행이 포함되었는지 AI에게 문의
    List<Long> userIds = new ArrayList<>();
    userIds.add(findReview.getUserId());
    String nickname = reviewClient.getUserNicknameList(userIds).get(0).nickname();

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

    List<Long> userIds = new ArrayList<>();
    userIds.add(findReview.getUserId());
    String nickname = reviewClient.getUserNicknameList(userIds).get(0).nickname();

    return ReviewResponseDto.fromEntity(findReview, nickname);
  }


  @Transactional(readOnly = true)
  public ReviewListResponseDto getReviews(UUID performanceId, int page, int size, boolean isAsc,
      String sortBy, String title, String content) {

    Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

    Page<Review> reviews = reviewRepository.findAllReviewByPageAndSortAndSearch(
        performanceId, pageable, title, content);

    List<Long> userIds = reviews.getContent().stream().map(Review::getUserId).toList();

    List<UserNicknameInfoDto> nicknames = reviewClient.getUserNicknameList(userIds).stream()
        .map(UserNicknameInfoDto::toUserNicknameInfoDto).toList();

    Page<ReviewResponseDto> reviewDtos = reviews.map(review -> {
      String nickname = nicknames.stream()
          .filter(nicknameInfo -> review.getUserId() == nicknameInfo.userId())
          .findFirst()
          .map(UserNicknameInfoDto::nickname)
          .orElse(null);
      return ReviewResponseDto.fromEntity(review, nickname);
    });

    return ReviewListResponseDto.of(reviewDtos, redisRatingRepository.getAvgRating(performanceId));
  }


}
