package com.ticketing.order.infrastructure;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

//@SpringBootTest
class RedisTemplateLearningTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @BeforeAll
    public static void setUp() {
        System.setProperty("username", "postgres");
    }

    @Test
    void registerWaitQueue() {
        redisTemplate.opsForZSet()
                .add("users:queue:wait", "test", System.currentTimeMillis());
    }

    @Test
    void find() {
        var rank = redisTemplate.opsForZSet().rank("waiting:queue", "sj3");
        System.out.println(rank);
    }


}