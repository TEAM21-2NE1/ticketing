package com.ticketing.performance.domain.repository;

import com.ticketing.performance.domain.model.Performance;
import com.ticketing.performance.domain.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SeatRepository extends JpaRepository<Seat, UUID> {
    List<Seat> findAllByPerformanceId(UUID performanceId);

    @Modifying
    @Query("UPDATE Seat s SET s.isDeleted = true WHERE s.performanceId = :performanceId")
    int softDeleteSeatsByPerformanceId(@Param("performanceId") UUID performanceId);

    @Modifying
    @Query("UPDATE Seat s SET s.price = :price WHERE s.performanceId = :performanceId and s.seatType = :seatType")
    int updateSeatPriceBySeatType(@Param("seatType")String seatType, @Param("price") Integer price, @Param("performanceId") UUID performanceId);

    @Query("Select s from Seat s where s.id in :seatIds")
    List<Seat> findAllByIds(@Param("seatIds") List<UUID> seatIds);

    List<Seat> findAllByPerformanceIdIn(List<UUID> performanceIds);

    boolean existsByPerformanceId(UUID performanceId);
}
