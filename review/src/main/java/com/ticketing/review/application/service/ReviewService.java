package com.ticketing.review.application.service;

import com.ticketing.review.application.client.ReviewClient;
import com.ticketing.review.application.client.dto.PerformanceInfoDto;
import com.ticketing.review.application.dto.request.CreateReviewRequestDto;
import com.ticketing.review.application.dto.request.UpdateReviewRequestDto;
import com.ticketing.review.application.dto.response.CreateReviewResponseDto;
import com.ticketing.review.application.dto.response.DeleteReviewResponseDto;
import com.ticketing.review.application.dto.response.ReviewListResponseDto;
import com.ticketing.review.application.dto.response.ReviewResponseDto;
import com.ticketing.review.application.dto.response.UpdateReviewResponseDto;
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
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final RedisRatingRepository redisRatingRepository;
  private final ReviewClient reviewClient;
  private final EventService eventService;


  /**
   * 리뷰 생성
   *
   * @param requestDto
   * @return
   */
  @CacheEvict(cacheNames = "reviewSearchCache", allEntries = true, cacheManager = "reviewCacheManager")
  @Transactional(readOnly = false)
  public CreateReviewResponseDto createReview(CreateReviewRequestDto requestDto) {
    // 같은 공연에 대해 리뷰를 등록함
    if (reviewRepository.existsByUserIdAndPerformanceId(SecurityUtils.getUserId(),
        requestDto.performanceId())) {
      throw new ReviewException(ErrorCode.ALREADY_EXISTS);
    }

    try {
      if (!reviewClient.getOrderStatus(SecurityUtils.getUserId(), requestDto.performanceId())
          .orderStatus()
          .equals("COMPLETED")) {
        throw new ReviewException(ErrorCode.ORDER_NOT_COMPLETED);
      }
    } catch (FeignException.Forbidden e) {
      throw new ReviewException(ErrorCode.ORDER_NOT_FOUND);
    }

    try {
      PerformanceInfoDto prfInfo = reviewClient.getPerformanceInfo(requestDto.performanceId());
      if (LocalDateTime.now().isBefore(prfInfo.performanceTime())) {
        throw new ReviewException(ErrorCode.EARLY_REVIEW);
      }
    } catch (FeignException.NotFound e) {
      throw new ReviewException(ErrorCode.PERFORMANCE_NOT_FOUND);
    }

    List<Long> userIds = new ArrayList<>();

    userIds.add(SecurityUtils.getUserId());
    String nickname = reviewClient.getUserNicknameList(userIds).get(SecurityUtils.getUserId());
    Review savedReview = reviewRepository.save(
        CreateReviewRequestDto.toEntity(requestDto, SecurityUtils.getUserId()));

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
  @Caching(evict = {
      @CacheEvict(cacheNames = "reviewCache", key = "#reviewId", cacheManager = "reviewCacheManager"),
      @CacheEvict(cacheNames = {
          "reviewSearchCache"}, allEntries = true, cacheManager = "reviewCacheManager")
  })
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

    List<Long> userIds = new ArrayList<>();
    userIds.add(findReview.getUserId());
    String nickname = reviewClient.getUserNicknameList(userIds).get(findReview.getUserId());

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
  @Caching(evict = {
      @CacheEvict(cacheNames = "reviewCache", key = "#reviewId", cacheManager = "reviewCacheManager"),
      @CacheEvict(cacheNames = {
          "reviewSearchCache"}, allEntries = true, cacheManager = "reviewCacheManager")
  })
  public DeleteReviewResponseDto deleteReview(UUID reviewId) {
    //DB에 reviewId에 대한 정보가 존재하는지 확인
    Review findReview = reviewRepository.findById(reviewId).orElseThrow(
        () -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND)
    );

    if (SecurityUtils.getUserRole().equals("ROLE_USER")
        && SecurityUtils.getUserId() != findReview.getUserId()) {
      throw new ReviewException(ErrorCode.REVIEW_FORBIDDEN);
    }

    findReview.deleteReview(SecurityUtils.getUserId());

    return DeleteReviewResponseDto.fromEntity(findReview);
  }


  /**
   * 리뷰 단건 조회
   *
   * @param reviewId
   * @return
   */
  @Transactional(readOnly = true)
  @Cacheable(cacheNames = "reviewCache", key = "#reviewId", cacheManager = "reviewCacheManager")
  public ReviewResponseDto getReview(UUID reviewId) {
    Review findReview = reviewRepository.findById(reviewId).orElseThrow(
        () -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND)
    );

    List<Long> userIds = new ArrayList<>();
    userIds.add(findReview.getUserId());
    String nickname = reviewClient.getUserNicknameList(userIds).get(findReview.getUserId());

    return ReviewResponseDto.fromEntity(findReview, nickname);
  }


  @Transactional(readOnly = true)
  @Cacheable(
      cacheNames = "reviewSearchCache",
      key = "{ #performanceId, #page, #size, #isAsc, #sortBy, #title, #content}",
      cacheManager = "reviewCacheManager"

  )
  public ReviewListResponseDto getReviews(UUID performanceId, int page, int size, boolean isAsc,
      String sortBy, String title, String content) {

    if (performanceId == null) {
      throw new ReviewException(ErrorCode.REQUIRED_PERFORMANCE);
    }

    Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

    Page<Review> reviews = reviewRepository.findAllReviewByPageAndSortAndSearch(
        performanceId, pageable, title, content);

    List<Long> userIds = reviews.getContent().stream().map(Review::getUserId).toList();

    Map<Long, String> nicknames = reviewClient.getUserNicknameList(userIds);

    Page<ReviewResponseDto> reviewDtos = reviews.map(review -> {
      String nickname = nicknames.get(review.getUserId());
      return ReviewResponseDto.fromEntity(review, nickname);
    });

    return ReviewListResponseDto.of(reviewDtos, redisRatingRepository.getAvgRating(performanceId));
  }


  /**
   * 공연 취소 시 리뷰 삭제
   *
   * @param performanceId
   * @param userId
   */
  @Transactional(readOnly = false)
  @Caching(evict = {
      @CacheEvict(cacheNames = "reviewCache", allEntries = true, cacheManager = "reviewCacheManager"),
      @CacheEvict(cacheNames = {
          "reviewSearchCache"}, allEntries = true, cacheManager = "reviewCacheManager")
  })
  public void deleteReviewByPerformance(UUID performanceId, Long userId) {
    try {
      reviewRepository.deleteByPerformanceId(performanceId, userId);

    } catch (Exception e) {
      eventService.publishReviewDeleteErrorEvent(performanceId, userId);
    }
  }


}
