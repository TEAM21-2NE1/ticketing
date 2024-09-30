package com.ticketing.performance.domain.model;

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
public class Hall {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String hallName;
    private String hallAddress;

    @OneToMany(mappedBy = "hall")
    private List<HallSeat> hallSeats = new ArrayList<>();

}
