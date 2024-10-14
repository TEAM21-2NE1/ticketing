package com.ticketing.order.domain.model;

public interface WaitingQueue {

    // 대기 등록
    WaitingTicket register(User user);

    Long size();

    User pop();

    WaitingTicket getTicket(User user);
}
