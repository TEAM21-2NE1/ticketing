package com.ticketing.performance.domain.repository;

import com.ticketing.performance.domain.model.Performance;
import com.ticketing.performance.infrastructure.repository.CustomPerformanceRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PerformanceRepository extends JpaRepository<Performance, UUID>, CustomPerformanceRepository {


    List<Performance> findAllByTicketOpenTimeBetween(LocalDateTime starOfDay, LocalDateTime endOfDay);
}
