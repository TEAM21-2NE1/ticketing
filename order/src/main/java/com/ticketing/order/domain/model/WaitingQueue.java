package com.ticketing.order.domain.model;

public interface WaitingQueue {

    // 대기 등록
    WaitingTicket register(User user);

    Long size();

    User pop();

    // 대기 번호 조회
    WaitingTicket getTicket(User user);

    void clear();

}
