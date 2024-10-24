package com.ticketing.performance.application.service;

import com.ticketing.performance.application.dto.rank.RankingResponseDto;
import com.ticketing.performance.domain.repository.RankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankService {

    private final RankRepository rankRepository;


    @Cacheable(cacheNames = "getRank", key = "'allRankings'", cacheManager = "rankCacheManager")
    public List<RankingResponseDto> getRank() {
        List<RankingResponseDto> ranks = rankRepository.getRank();

        AtomicInteger rankCounter = new AtomicInteger(1);
        AtomicInteger previousRankCounter = new AtomicInteger(1);
        Double previousRate = null;


        for (RankingResponseDto rank : ranks) {
            Double currentRate = rank.getReservationRate();

            if (previousRate != null && previousRate.equals(currentRate)) {
                rank.setRanking(previousRankCounter.get());
            } else {
                previousRankCounter.set(rankCounter.get());
                rank.setRanking(rankCounter.get());
            }
            previousRate = currentRate;
            rankCounter.incrementAndGet();
        }
        return ranks;
    }

}
