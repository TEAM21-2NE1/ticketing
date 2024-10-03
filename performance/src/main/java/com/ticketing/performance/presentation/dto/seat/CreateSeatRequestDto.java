package com.ticketing.performance.presentation.dto.seat;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateSeatRequestDto {

    @NotBlank
    private UUID performanceId;
    @NotBlank
    private UUID hallId;
    @NotNull
    private List<SeatTypePriceRequestDto> sections;



}
