package com.ticketing.order.infrastructure;

import com.ticketing.order.domain.model.User;
import com.ticketing.order.domain.model.WaitingQueue;
import com.ticketing.order.domain.model.WaitingTicket;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisWaitingQueue implements WaitingQueue {

    public static final String WAITING_QUEUE = "waiting:queue";
    public static final int POP_COUNT = 1;
    private final RedisTemplate<String, User> redisTemplate;

    // 대기열 등록
    @Override
    public WaitingTicket register(User user) {
        var existRank = redisTemplate.opsForZSet().rank(WAITING_QUEUE, user);
        if (existRank != null) {
            return WaitingTicket.of(existRank);
        }

        redisTemplate.opsForZSet()
                .addIfAbsent(WAITING_QUEUE, user, System.currentTimeMillis());

        var rank = redisTemplate.opsForZSet().rank(WAITING_QUEUE, user);
        WaitingTicket waitingTicket = WaitingTicket.of(rank);

        return waitingTicket;
    }

    @Override
    public Long size() {
        return redisTemplate.opsForZSet().size(WAITING_QUEUE);
    }

    @Override
    public User pop() {
        Set<TypedTuple<User>> users = redisTemplate.opsForZSet().popMin(WAITING_QUEUE, POP_COUNT);

        if (users.isEmpty()) {
            return null;
        } else {
            return users.stream().toList().get(0).getValue();
        }
    }

    // 대기 번호 조회
    @Override
    public WaitingTicket getTicket(User user) {
        Long rank = redisTemplate.opsForZSet().rank(WAITING_QUEUE, user);
        return WaitingTicket.of(rank);
    }
}
