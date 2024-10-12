package com.ticketing.order.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.ticketing.order.common.exception.OrderException;
import com.ticketing.order.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class RedisRunningQueueIntegrationTest {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RedisRunningQueue runningQueue;

    @Test
    void 큐의_등록된_유저의_수를_조회한다() {
        User user1 = User.of("1");
        User user2 = User.of("2");

        runningQueue.push(user1);
        runningQueue.push(user2);

        assertEquals(2, getSize());
    }

    @Test
    void 큐에_등록된_유저를_제거한다() {
        User user3 = User.of("3");
        runningQueue.push(user3);
        assertEquals(3, getSize());

        runningQueue.remove(user3);
        assertEquals(2, getSize());
    }

    @Test
    void 큐에_허용된_사용자를_초과하면_오류가_발생해야한다() {
        User user4 = User.of("4");

        Executable executable = () -> {
            runningQueue.push(user4);
        };

        assertThrows(OrderException.class, executable);
    }

    private Long getSize() {
        return redisTemplate.opsForHash().size("running:queue");
    }

    @Test
    void 허용된_사용자_체크() {
        User user1 = User.of("1");
        boolean check = runningQueue.check(user1);
        assertEquals(true, check);
    }
}