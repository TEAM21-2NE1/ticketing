package com.ticketing.performance.presentation.dto.hall;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateHallRequestDto {

    private String hallName;
    private String hallAddress;
}
