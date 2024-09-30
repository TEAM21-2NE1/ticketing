package com.ticketing.performance.domain.repository;

import com.ticketing.performance.domain.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SeatRepository extends JpaRepository<Seat, UUID> {
    List<Seat> findAllByPerformanceId(UUID performanceId);
}
