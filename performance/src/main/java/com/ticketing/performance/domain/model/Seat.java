package com.ticketing.performance.domain.model;

import com.ticketing.performance.common.auditor.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Table(name = "p_seat")
@Entity
@SQLRestriction("is_deleted = false")
public class Seat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Integer price;

    private UUID performanceId;

    private UUID orderId;

    private String seatType;
    private Integer seatNum;
    private Integer seatRow;

    @Enumerated(value = EnumType.STRING)
    private SeatStatus seatStatus;

}
