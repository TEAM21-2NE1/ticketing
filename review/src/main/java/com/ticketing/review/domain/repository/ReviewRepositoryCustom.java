package com.ticketing.review.domain.repository;

import com.ticketing.review.domain.model.Review;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ReviewRepositoryCustom {

  Page<Review> findAllReviewByPageAndSortAndSearch(UUID performanceId, Pageable pageable,
      String title, String content);
}
