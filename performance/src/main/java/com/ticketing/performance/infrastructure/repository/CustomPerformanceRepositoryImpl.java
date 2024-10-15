package com.ticketing.performance.infrastructure.repository;

import static com.ticketing.performance.domain.model.QPerformance.performance;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ticketing.performance.domain.model.Performance;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class CustomPerformanceRepositoryImpl implements CustomPerformanceRepository{

    private final JPAQueryFactory queryFactory;

    public CustomPerformanceRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Page<Performance> findAllByKeywordByManagerId(String keyword, Pageable pageable, Long managerId) {
        List<Performance> performances = queryFactory
                .select(performance)
                .from(performance)
                .where(
                        containsKeyword(keyword),
                        performance.managerId.eq(managerId)
                ).offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifiers(pageable))
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(performance.count())
                .from(performance)
                .where(
                        containsKeyword(keyword),
                        performance.managerId.eq(managerId)
                        );


        return PageableExecutionUtils.getPage(performances, pageable, countQuery::fetchOne);

    }


    public Page<Performance> findAllByKeywordByUser(String keyword, Pageable pageable) {
        List<Performance> performances = queryFactory
                .select(performance)
                .from(performance)
                .where(
                        containsKeyword(keyword),
                        performance.openDate.loe(LocalDate.now())
                ).offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifiers(pageable))
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(performance.count())
                .from(performance)
                .where(containsKeyword(keyword),
                        performance.openDate.before(LocalDate.now()));


        return PageableExecutionUtils.getPage(performances, pageable, countQuery::fetchOne);

    }


    public Page<Performance> findAllByKeyword(String keyword, Pageable pageable) {
        List<Performance> performances = queryFactory
                .select(performance)
                .from(performance)
                .where(containsKeyword(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifiers(pageable))
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(performance.count())
                .from(performance)
                .where(containsKeyword(keyword));

        return PageableExecutionUtils.getPage(performances, pageable, countQuery::fetchOne);

    }


    private BooleanExpression containsKeyword(String keyword) {
        if (!StringUtils.hasText(keyword) || keyword.isEmpty()) {
            return null;
        }
        return performance.title.contains(keyword).or(
                performance.description.contains(keyword));
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier<?>> list = new ArrayList<>();
        for (Sort.Order o : pageable.getSort()) {
            PathBuilder<Performance> pathBuilder = new PathBuilder<>(performance.getType(),
                    performance.getMetadata());
            list.add(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC,
                     pathBuilder.get(o.getProperty())));
        }
        return list.toArray(OrderSpecifier[]::new);
    }


}
