package com.ticketing.order.infrastructure.redis;

import com.ticketing.order.application.dto.client.SeatInfoResponseDto;
import com.ticketing.order.application.service.SeatOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class RedisKeyExpiredListener extends KeyExpirationEventMessageListener {

    private final SeatOrderService seatOrderService;

    public RedisKeyExpiredListener(RedisMessageListenerContainer listenerContainer, SeatOrderService seatOrderService)
    {
        super(listenerContainer);
        this.seatOrderService = seatOrderService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageToStr = message.toString();
        if (messageToStr.startsWith("seatTTL:")) {
            UUID performanceId = UUID.fromString(messageToStr.split(":")[1]);
            UUID seatId = UUID.fromString(messageToStr.split(":")[2]);
            Long userId = Long.parseLong(messageToStr.split(":")[3]);
            SeatInfoResponseDto seatFromRedis = seatOrderService.getSeatFromRedis(performanceId, seatId);
            seatFromRedis.cancel();
            seatOrderService.releaseExpiredSeats(performanceId, seatId, seatFromRedis, userId);

        }
    }
}
