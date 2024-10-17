package com.ticketing.review.infrastructure.repository;

import static com.ticketing.review.domain.model.QReview.review;
import static org.springframework.util.StringUtils.hasText;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ticketing.review.domain.model.Review;
import com.ticketing.review.domain.repository.ReviewRepositoryCustom;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

  private final JPAQueryFactory queryFactory;


  @Override
  public Page<Review> findAllReviewByPageAndSortAndSearch(UUID performanceId, Pageable pageable,
      String title, String content) {

    JPAQuery<Review> query = queryFactory
        .selectFrom(review)
        .where(review.performanceId.eq(performanceId), titleContain(title), contentContain(content))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize());

    for (Sort.Order order : pageable.getSort()) {
      PathBuilder<Review> path = new PathBuilder<>(Review.class, "review");
      query.orderBy(new OrderSpecifier(
          com.querydsl.core.types.Order.valueOf(order.getDirection().name()),
          path.get(order.getProperty())
      ));
    }
    List<Review> list = query.fetch();

    JPAQuery<Long> countQuery = queryFactory
        .select(review.count())
        .from(review)
        .where(titleContain(title), contentContain(content));

    return PageableExecutionUtils.getPage(list, pageable, countQuery::fetchOne);
  }

  private BooleanExpression titleContain(String title) {
    return !hasText(title) ? null : review.title.contains(title);
  }

  private BooleanExpression contentContain(String content) {
    return !hasText(content) ? null : review.content.contains(content);
  }


}
