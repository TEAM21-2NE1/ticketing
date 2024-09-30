package com.ticketing.performance.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Table(name = "p_hall_seat")
public class HallSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String seatType;
    private Integer seatRow;
    private Integer seatNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id")
    private Hall hall;
}
