package com.ticketing.performance.infrastructure.repository;

import com.ticketing.performance.common.util.SecurityUtil;
import com.ticketing.performance.domain.model.Seat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class SeatJdbcRepositoryImpl implements SeatJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public SeatJdbcRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveAll(List<Seat> seats) {
        String sql = "INSERT INTO p_seat (created_at, created_by, deleted_at, deleted_by, is_deleted, order_id, performance_id, price, seat_num, seat_row, seat_status, seat_type, updated_at, updated_by, id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> batchArgs = seats.stream()
                .map(seat -> new Object[] {
                        LocalDateTime.now(), SecurityUtil.getId(), seat.getDeletedAt(), seat.getDeletedBy(), seat.getIsDeleted(),
                        seat.getOrderId(), seat.getPerformanceId(), seat.getPrice(), seat.getSeatNum(), seat.getSeatRow(),
                        seat.getSeatStatus().name(), seat.getSeatType(), LocalDateTime.now(), SecurityUtil.getId(), UUID.randomUUID()
                })
                .toList();

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}