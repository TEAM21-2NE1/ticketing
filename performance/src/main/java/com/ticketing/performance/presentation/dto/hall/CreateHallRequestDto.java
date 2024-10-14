package com.ticketing.performance.presentation.dto.hall;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateHallRequestDto {

    @NotBlank
    private String hallName;
    @NotBlank
    private String hallAddress;
    @NotNull
    private List<CreateHallSeatRequestDto> sections;
}
