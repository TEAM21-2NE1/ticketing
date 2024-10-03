package com.ticketing.performance.presentation.dto.hall;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateHallRequestDto {

    @NotBlank
    private String hallName;
    @NotBlank
    private String hallAddress;
}
