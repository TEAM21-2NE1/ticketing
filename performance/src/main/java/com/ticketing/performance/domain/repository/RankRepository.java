package com.ticketing.performance.domain.repository;

import com.ticketing.performance.application.dto.rank.RankingResponseDto;

import java.util.List;

public interface RankRepository {

    List<RankingResponseDto> getRank();
}
