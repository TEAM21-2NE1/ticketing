package com.ticketing.performance.presentation.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateHallRequestDto {

    private String hallName;
    private String hallAddress;
    private List<CreateHallSeatRequestDto> sections;
}
