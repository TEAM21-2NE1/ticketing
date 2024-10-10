package com.ticketing.order.domain.model;

public interface RunningQueue {

    void push(User user);

    void remove(User user);

    boolean available();

    boolean check(User user);

    Long size();
}
