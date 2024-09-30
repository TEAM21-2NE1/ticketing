package com.ticketing.performance.presentation.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateHallSeatRequestDto {

    private String seatType;
    private Integer rows;
    private Integer seatsPerRow;
}
