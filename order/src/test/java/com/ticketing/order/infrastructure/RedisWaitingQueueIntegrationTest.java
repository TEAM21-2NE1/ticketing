package com.ticketing.order.infrastructure;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ticketing.order.domain.model.User;
import com.ticketing.order.domain.model.WaitingQueue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;


//@SpringBootTest
class RedisWaitingQueueIntegrationTest {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    WaitingQueue waitingQueue;

    private static final String WAITING_QUEUE_KEY = "waiting:queue";
    private static final String PROCEED_QUEUE_KEY = "proceed:queue";

    @BeforeAll
    public static void setUp() {
        System.setProperty("username", "postgres");
    }

    @BeforeEach
    void clearQueue() {
        redisTemplate.delete(WAITING_QUEUE_KEY);
        redisTemplate.delete(PROCEED_QUEUE_KEY);
    }

    @Test
    void 유저두명을_등록하면_첫번째유저는_순서대로_대기번호를_받는다() {
        var watingTicket = waitingQueue.register(User.of("sj"));
        var watingTicket2 = waitingQueue.register(User.of("sj2"));

        assertEquals(0, watingTicket.getOrder());
        assertEquals(1, watingTicket2.getOrder());

    }

    @Test
    void 기존에_등록된유저가_등록을_시도하면_기존대기번호를_받는다() {
        var watingTicket = waitingQueue.register(User.of("sj"));
        var watingTicket2 = waitingQueue.register(User.of("sj2"));
        var watingTicket3 = waitingQueue.register(User.of("sj"));

        assertEquals(0, watingTicket.getOrder());
        assertEquals(1, watingTicket2.getOrder());
        assertEquals(0, watingTicket3.getOrder());


    }

    @Test
    void 가장_먼저_대기한_유저를_꺼낸다() {
        User user1 = User.of("1");
        User user2 = User.of("2");

        waitingQueue.register(user1);
        waitingQueue.register(user2);

        User user = waitingQueue.pop();

        assertEquals(user1, user);
    }


    @Test
    void 유저넣기() {
        User user = User.of("4");

        waitingQueue.register(user);
    }

}