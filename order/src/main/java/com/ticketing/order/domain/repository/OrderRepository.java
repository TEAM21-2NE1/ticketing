package com.ticketing.order.domain.repository;

import com.ticketing.order.domain.model.Order;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {

}
