package com.ticketing.order.infrastructure;

import com.ticketing.order.common.exception.OrderException;
import com.ticketing.order.common.response.ExceptionMessage;
import com.ticketing.order.domain.model.RunningQueue;
import com.ticketing.order.domain.model.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
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
        // Redis WATCH-MULTI-EXEC를 사용하여 트랜잭션 처리
        Boolean result = redisTemplate.execute(new SessionCallback<Boolean>() {
            @Override
            @SuppressWarnings("unchecked")
            public Boolean execute(RedisOperations operations) throws DataAccessException {
                operations.watch(RUNNING_QUEUE);

                Long currentSize = operations.opsForHash().size(RUNNING_QUEUE);
                if (currentSize >= MAX_CAPACITY) {
                    operations.unwatch();
                    throw new OrderException(ExceptionMessage.CAPACITY_EXCEED);
                }

                operations.multi();
                operations.opsForHash().putIfAbsent(RUNNING_QUEUE, user.getUserId(), user);
                List<Object> exec = operations.exec();

                return !exec.isEmpty();
            }
        });

        if (result == null || !result) {
            throw new OrderException(ExceptionMessage.CAPACITY_EXCEED);
        }
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
