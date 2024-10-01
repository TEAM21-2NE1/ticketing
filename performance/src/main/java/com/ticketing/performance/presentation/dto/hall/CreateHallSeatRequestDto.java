package com.ticketing.performance.presentation.dto.hall;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateHallSeatRequestDto {

    @NotBlank
    private String seatType;
    @NotBlank
    private Integer rows;
    @NotBlank
    private Integer seatsPerRow;
}
