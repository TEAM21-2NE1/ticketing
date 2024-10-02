package com.ticketing.performance.domain.repository;

import com.ticketing.performance.domain.model.Hall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface HallRepository extends JpaRepository<Hall, UUID> {

    @EntityGraph(attributePaths = {"hallSeats"})
    Optional<Hall> findById(UUID id);

    @EntityGraph(attributePaths = {"hallSeats"})
    Page<Hall> findAll(Pageable pageable);
}
