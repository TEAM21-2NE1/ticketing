package com.ticketing.order.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
@Getter
@Builder
public class WaitingTicket {

    private final Long order;// 대기 순번
    private final Long behindCount;

    public static WaitingTicket of(Long order) {
        return WaitingTicket.builder().order(order).build();
    }

}
