package com.ticketing.performance.domain.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ticketing.performance.domain.model.SeatStatus;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.ticketing.performance.domain.model.QPerformance.performance;
import static com.ticketing.performance.domain.model.QSeat.seat;

@Repository
public class RankRepositoryImpl implements RankRepository{

    private final JPAQueryFactory queryFactory;

    public RankRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }


    public List<Tuple> getRank() {
        return queryFactory
                .select(
                        performance.id,
                        performance.title,
                        performance.posterUrl,
                        performance.performanceTime,
                        Expressions.cases()
                                .when(seat.count().eq(0L))
                                .then(0.0)
                                .otherwise(
                                        seat.seatStatus.when(SeatStatus.BOOKED).then(1).otherwise(0).sum().doubleValue()
                                                .divide(seat.count()).multiply(100.0)
                                ).as("reservationRate")
                )
                .from(performance)
                .leftJoin(seat).on(seat.performanceId.eq(performance.id))
                .groupBy(performance.id, performance.title, performance.posterUrl, performance.performanceTime)
                .orderBy(Expressions.numberPath(Double.class, "reservationRate").desc())
                .limit(50)
                .fetch();


    }
}
