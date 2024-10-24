package com.ticketing.order.domain.repository;

import com.ticketing.order.domain.model.Order;
import feign.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, UUID> {


    Optional<Order> findByUserIdAndPerformanceId(String userId, UUID performanceId);

    @Query("SELECT o.performanceId FROM Order o WHERE o.userId = :userId AND o.isDeleted = false")
    List<UUID> findPerformanceIdByUserIdAndIsDeletedIsFalse(String userId);

    @Query("SELECT o FROM Order o JOIN o.selectedSeatIds s WHERE s IN :selectedSeatIds and o.userId = :userId")
    Order findBySelectedSeatIds(@Param("selectedSeatIds") List<UUID> selectedSeatIds,
            @Param("userId") String userId);
}
