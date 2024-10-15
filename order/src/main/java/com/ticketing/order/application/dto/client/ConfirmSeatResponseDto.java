package com.ticketing.order.application.dto.client;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ConfirmSeatResponseDto {

    private List<ConfirmSeatDto> confirmedSeats;

}
