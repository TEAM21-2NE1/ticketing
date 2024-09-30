package com.ticketing.performance.domain.repository;

import com.ticketing.performance.domain.model.Hall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HallRepository extends JpaRepository<Hall, UUID> {

}
