package com.ticketing.performance.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Table(name = "p_seat")
@Entity
public class Seat {

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
