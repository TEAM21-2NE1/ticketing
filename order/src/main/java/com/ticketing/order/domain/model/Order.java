package com.ticketing.order.domain.model;

import com.ticketing.order.application.dto.request.CreateOrderRequestDto;
import com.ticketing.order.common.auditor.BaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "p_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("is_deleted = false")
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

    @ElementCollection
    @CollectionTable(name = "order_selected_seats", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "seat_id")
    private List<UUID> selectedSeatIds = new ArrayList<>();

    public static Order of(CreateOrderRequestDto requestDto, String userId, Integer totalAmount) {
        return Order.builder()
                .paymentMethod(PaymentMethod.valueOf(requestDto.paymentMethod()))
                .totalAmount(totalAmount)
                .performanceId(requestDto.performanceId())
                .orderStatus(OrderStatus.PENDING_PAYMENT)
                .userId(userId)
                .selectedSeatIds(requestDto.selectedSeatIds())
                .build();
    }

    public static Order createOrder(UUID paymentId, UUID performanceId, Integer totalAmount,
            PaymentMethod paymentMethod, String userId, List<UUID> selectedSeatIds) {
        return Order.builder()
                .paymentId(paymentId)
                .performanceId(performanceId)
                .totalAmount(totalAmount)
                .paymentMethod(paymentMethod)
                .orderStatus(OrderStatus.PENDING_PAYMENT)
                .userId(userId)
                .selectedSeatIds(selectedSeatIds)
                .build();
    }

    public void successUpdate(OrderStatus status,UUID paymentId) {
        this.orderStatus = status;
        this.paymentId = paymentId;
    }

    public void delete(Long id) {
        super.setDeleted(id);
    }

    public void cancel() {
        this.orderStatus = OrderStatus.CANCELED;
    }
}