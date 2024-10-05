package com.ticketing.performance.application.service;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.ticketing.performance.application.dto.rank.RankingResponseDto;
import com.ticketing.performance.domain.repository.RankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ticketing.performance.domain.model.QPerformance.performance;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankService {

    private final RankRepository rankRepository;


    @Cacheable(cacheNames = "getRank", key = "'allRankings'")
    public List<RankingResponseDto> getRank() {
        List<Tuple> fetch = rankRepository.getRank();

        List<RankingResponseDto> rankingResults = new ArrayList<>();
        AtomicInteger rankCounter = new AtomicInteger(1);
        AtomicInteger previousRankCounter = new AtomicInteger(1);
        Double previousRate = null;


        for (Tuple tuple : fetch) {
            Double currentRate = tuple.get(Expressions.numberPath(Double.class, "reservationRate"));

            if (previousRate != null && previousRate.equals(currentRate)) {
                rankingResults.add(
                        RankingResponseDto.builder()
                                .performanceId(tuple.get(performance.id))
                                .title(tuple.get(performance.title))
                                .posterUrl(tuple.get(performance.posterUrl))
                                .performanceTime(tuple.get(performance.performanceTime))
                                .reservationRate(currentRate)
                                .ranking(previousRankCounter.get())
                                .build()
                );
            } else {
                previousRankCounter.set(rankCounter.get());
                rankingResults.add(
                        RankingResponseDto.builder()
                                .performanceId(tuple.get(performance.id))
                                .title(tuple.get(performance.title))
                                .posterUrl(tuple.get(performance.posterUrl))
                                .performanceTime(tuple.get(performance.performanceTime))
                                .reservationRate(currentRate)
                                .ranking(rankCounter.get())
                                .build()
                );
            }
            previousRate = currentRate;
            rankCounter.incrementAndGet();
        }
        return rankingResults;
    }

}
