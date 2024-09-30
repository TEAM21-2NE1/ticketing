package com.ticketing.performance.domain.model;

import com.ticketing.performance.common.auditor.BaseEntity;
import com.ticketing.performance.presentation.dto.CreateHallRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "p_hall")
public class Hall extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String hallName;
    private String hallAddress;

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HallSeat> hallSeats = new ArrayList<>();


    public static Hall create(CreateHallRequestDto createHallRequestDto) {
        return com.ticketing.performance.domain.model.Hall.builder()
                .hallName(createHallRequestDto.getHallName())
                .hallAddress(createHallRequestDto.getHallAddress())
                .build();
    }

    public void addSeats(List<HallSeat> seats) {
        this.hallSeats= seats;
        seats.forEach(seat -> seat.addHall(this));
    }
}