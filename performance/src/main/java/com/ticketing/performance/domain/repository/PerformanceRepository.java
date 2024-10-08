package com.ticketing.performance.domain.repository;

import com.ticketing.performance.domain.model.Performance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.UUID;

public interface PerformanceRepository extends JpaRepository<Performance, UUID>, CustomPerformanceRepository {

    Page<Performance> findAllByOpenDateBefore(LocalDate date, Pageable pageable);

}
