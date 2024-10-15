package com.ticketing.performance.infrastructure.repository;

import com.ticketing.performance.domain.model.Performance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomPerformanceRepository {

    Page<Performance> findAllByKeywordByUser(String keyword, Pageable pageable);

    Page<Performance> findAllByKeywordByManagerId(String keyword, Pageable pageable, Long managerId);

    Page<Performance> findAllByKeyword(String keyword, Pageable pageable);

}
