package com.ticketing.performance.infrastructure.repository;

import com.ticketing.performance.domain.model.Seat;

import java.util.List;

public interface SeatJdbcRepository {

    void saveAll(List<Seat> seats);
}
