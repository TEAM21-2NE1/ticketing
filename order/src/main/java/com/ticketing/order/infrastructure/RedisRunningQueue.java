package com.ticketing.order.infrastructure;

import com.ticketing.order.common.exception.OrderException;
import com.ticketing.order.common.response.ExceptionMessage;
import com.ticketing.order.domain.model.RunningQueue;
import com.ticketing.order.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisRunningQueue implements RunningQueue {

    public static final int MAX_CAPACITY = 10;
    public static final String RUNNING_QUEUE = "running:queue";
    private final RedisTemplate<String, User> redisTemplate;

    @Override
    public Long size() {
        var size = redisTemplate.opsForHash().size(RUNNING_QUEUE);
        return size;
    }

    @Override
    public void push(User user) {
        if (!available()) {
            throw new OrderException(ExceptionMessage.CAPACITY_EXCEED);
        }
        redisTemplate.opsForHash().putIfAbsent(RUNNING_QUEUE, user.getUserId(), user);
    }

    // 결제 완료되면 지움
    @Override
    public void remove(User user) {
        redisTemplate.opsForHash().delete(RUNNING_QUEUE, user.getUserId());
    }

    @Override
    public boolean available() {
        if (size() < MAX_CAPACITY) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean check(User user) {
        Object result = redisTemplate.opsForHash().get(RUNNING_QUEUE, user.getUserId());

        if (result != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void clear() {
        redisTemplate.delete(RUNNING_QUEUE);
    }
}
