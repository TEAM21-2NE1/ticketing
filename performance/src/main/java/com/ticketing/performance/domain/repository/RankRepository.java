package com.ticketing.performance.domain.repository;

import com.querydsl.core.Tuple;

import java.util.List;

public interface RankRepository {

    List<Tuple> getRank();
}
