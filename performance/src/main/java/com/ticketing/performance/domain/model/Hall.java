package com.ticketing.performance.domain.model;

import com.ticketing.performance.common.auditor.BaseEntity;
import com.ticketing.performance.presentation.dto.CreateHallRequestDto;
import com.ticketing.performance.presentation.dto.UpdateHallRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "p_hall")
@SQLRestriction("is_deleted = false")
public class Hall extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String hallName;
    private String hallAddress;

    @OneToMany(mappedBy = "hall",fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
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

    public Integer getTotalSeat() {
        return this.getHallSeats().stream().mapToInt(i -> i.getSeatsPerRow() * i.getRows()).sum();
    }

    public void update(UpdateHallRequestDto updateHallRequestDto) {
        this.hallName = updateHallRequestDto.getHallName();
        this.hallAddress = updateHallRequestDto.getHallAddress();
    }

    public void delete(Long id) {
        super.delete(id);
        hallSeats.forEach(hallSeat -> hallSeat.delete(id));
    }
}
