package com.ticketing.performance.domain.repository;

import com.ticketing.performance.domain.model.Performance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomPerformanceRepository {


    Page<Performance> findAllByKeyword(String keyword, Pageable pageable);
}
