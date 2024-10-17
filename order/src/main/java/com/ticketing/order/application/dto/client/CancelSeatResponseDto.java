package com.ticketing.order.application.dto.client;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CancelSeatResponseDto {

    private List<CancelSeatDto> canceledSeats;

}
