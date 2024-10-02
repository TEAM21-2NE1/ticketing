package com.ticketing.performance.domain.repository;

import com.ticketing.performance.domain.model.Performance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PerformanceRepository extends JpaRepository<Performance, UUID> {

}
