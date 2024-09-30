package com.ticketing.performance.domain.model;

import com.ticketing.performance.common.auditor.BaseEntity;
import com.ticketing.performance.presentation.dto.CreateHallSeatRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Table(name = "p_hall_seat")
@ToString
public class HallSeat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String seatType;
    private Integer rows;
    private Integer seatsPerRow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id")
    private Hall hall;

    public static HallSeat create(CreateHallSeatRequestDto requestDto) {
        return HallSeat.builder()
                .seatType(requestDto.getSeatType())
                .rows(requestDto.getRows())
                .seatsPerRow(requestDto.getSeatsPerRow())
                .build();
    }


    public void addHall(Hall hall) {
        this.hall = hall;
    }
}
