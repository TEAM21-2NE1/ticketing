package com.ticketing.order.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
@Getter
public class WaitingTicket {

    private final Long order; // 대기 순번

}
