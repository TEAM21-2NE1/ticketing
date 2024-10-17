package com.ticketing.performance.domain.repository;

import com.ticketing.performance.domain.model.Seat;
import com.ticketing.performance.infrastructure.repository.SeatJdbcRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SeatRepository extends JpaRepository<Seat, UUID>, SeatJdbcRepository {
    List<Seat> findAllByPerformanceId(UUID performanceId);

    @Modifying
    @Query("UPDATE Seat s SET s.isDeleted = true, s.deletedBy = :userId, s.deletedAt = CURRENT_TIMESTAMP  WHERE s.performanceId = :performanceId")
    void softDeleteSeatsByPerformanceId(@Param("performanceId") UUID performanceId, @Param("userId")Long userId);

    @Modifying
    @Query("UPDATE Seat s SET s.price = :price, s.updatedAt = CURRENT_TIMESTAMP , s.updatedBy = :userId WHERE s.performanceId = :performanceId and s.seatType = :seatType")
    void updateSeatPriceBySeatType(@Param("seatType")String seatType, @Param("price") Integer price, @Param("performanceId") UUID performanceId, @Param("userId")Long userId);

    @Query("Select s from Seat s where s.id in :seatIds")
    List<Seat> findAllByIds(@Param("seatIds") List<UUID> seatIds);

    List<Seat> findAllByPerformanceIdIn(List<UUID> performanceIds);

    boolean existsByPerformanceId(UUID performanceId);
}
