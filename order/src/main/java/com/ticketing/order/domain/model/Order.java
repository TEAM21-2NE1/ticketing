package com.ticketing.order.domain.model;

import com.ticketing.order.application.dto.request.CreateOrderRequestDto;
import com.ticketing.order.common.auditor.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "payment_id")
    private UUID paymentId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "performance_id", nullable = false)
    private UUID performanceId;

    @Column(name = "total_amount")
    private Integer totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    public static Order of(CreateOrderRequestDto requestDto, String userId) {
        return Order.builder()
                .paymentMethod(PaymentMethod.valueOf(requestDto.paymentMethod()))
                .performanceId(requestDto.performanceId())
                .orderStatus(OrderStatus.PENDING_PAYMENT)
                .userId(userId)
                .build();
    }

    public static Order createOrder(UUID paymentId, UUID performanceId, Integer totalAmount,
            PaymentMethod paymentMethod, String userId) {
        return Order.builder()
                .paymentId(paymentId)
                .performanceId(performanceId)
                .totalAmount(totalAmount)
                .paymentMethod(paymentMethod)
                .orderStatus(OrderStatus.PENDING_PAYMENT)
                .userId(userId)
                .build();
    }
}
