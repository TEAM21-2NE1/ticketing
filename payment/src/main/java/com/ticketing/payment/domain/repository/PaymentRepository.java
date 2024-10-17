package com.ticketing.payment.domain.repository;

import com.ticketing.payment.domain.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
