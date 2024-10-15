package com.ticketing.order.infrastructure;


import com.ticketing.order.application.dto.PrfRedisInfoDto;
import com.ticketing.order.application.dto.client.SeatInfoResponseDto;
import com.ticketing.order.application.service.SeatOrderService;
import com.ticketing.order.common.exception.OrderException;
import com.ticketing.order.common.exception.SeatException;
import com.ticketing.order.common.response.ExceptionMessage;
import com.ticketing.order.config.SecurityUtil;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatOrderHashServiceImpl implements SeatOrderService {

    private final RedisTemplate<String, Object> redisTemplateSeat;
    private final String TICKET_LIMIT = "ticketLimit";
    private final String TICKET_OPEN_TIME = "ticketOpenTime";

    public void saveSeatsToRedis(PrfRedisInfoDto prfRedisInfoDto,
            List<SeatInfoResponseDto> seatList) {
        String key = generateKey(prfRedisInfoDto.getPerformanceId());
        Map<String, SeatInfoResponseDto> seatMap = seatList.stream()
                .collect(Collectors.toMap(
                        seat -> seat.getSeatId().toString(),
                        seat -> seat
                ));
        redisTemplateSeat.opsForHash().putAll(key, seatMap);
        redisTemplateSeat.opsForHash()
                .put(key, TICKET_LIMIT, String.valueOf(prfRedisInfoDto.getTicketLimit()));
        redisTemplateSeat.opsForHash()
                .put(key, TICKET_OPEN_TIME, prfRedisInfoDto.getTicketOpenTime().toString());

        Duration ttlDuration = Duration.between(
                LocalDateTime.now(), prfRedisInfoDto.getPerformanceTime().plusDays(1));

        if (ttlDuration.isNegative() || ttlDuration.isZero()) {
            redisTemplateSeat.delete(key);
        }

        redisTemplateSeat.expire(key, ttlDuration);

    }

    public void holdSeat(UUID performanceId, UUID seatId) {

        SeatInfoResponseDto seat = (SeatInfoResponseDto) redisTemplateSeat.opsForHash()
                .get(generateKey(performanceId), seatId.toString());
        if (seat == null) {
            throw new SeatException(ExceptionMessage.SEAT_NOT_FOUND);
        }

        switch (seat.getSeatStatus()) {
            case AVAILABLE -> {
                if (getSelectedSeatsCount(performanceId) >= getTicketLimit(performanceId)) {
                    throw new SeatException(ExceptionMessage.SEAT_SELECT_EXCEEDED);
                }
                seat.hold();
                redisTemplateSeat.opsForHash().put(generateKey(performanceId), seatId.toString(), seat);
            }
            case HOLD -> {
                if (!seat.getUserId().equals(SecurityUtil.getId())) {
                    throw new SeatException(ExceptionMessage.SEAT_ALREADY_HOLD, getSeatsFromRedis(performanceId));
                }
                seat.cancel();
                redisTemplateSeat.opsForHash().put(generateKey(performanceId), seatId.toString(), seat);
            }
            case BOOKED -> throw new SeatException(ExceptionMessage.SEAT_ALREADY_BOOKED);

            default -> throw new SeatException(ExceptionMessage.SEAT_STATUS_NOT_EXIST);
        }
    }

    public SeatInfoResponseDto getSeatFromRedis(UUID performanceId, UUID seatId) {
        Object seat = redisTemplateSeat.opsForHash().get(generateKey(performanceId), seatId.toString());
        return seat != null ? (SeatInfoResponseDto) seat : null;
    }

    public List<SeatInfoResponseDto> getSeatsFromRedis(UUID performanceId) {
        String key = generateKey(performanceId);
        String openTimeStr = (String) redisTemplateSeat.opsForHash().get(key, TICKET_OPEN_TIME);

        if (openTimeStr != null && LocalDateTime.now().isBefore(LocalDateTime.parse(openTimeStr))) {
            throw new SeatException(ExceptionMessage.TICKET_NOT_OPEN);
        }
        Map<Object, Object> seatMap = redisTemplateSeat.opsForHash().entries(key);
        seatMap.remove(TICKET_LIMIT);
        seatMap.remove(TICKET_OPEN_TIME);
        return seatMap.values().stream()
                .map(value -> (SeatInfoResponseDto) value)
                .toList();
    }

    @Override
    public void confirm(List<UUID> seatIds, UUID performanceId) {
        seatIds.forEach(seatId -> {
            SeatInfoResponseDto seatInfo = getSeatFromRedis(performanceId, seatId);

            if (seatInfo == null) {
                throw new SeatException(ExceptionMessage.SEAT_NOT_FOUND);
            }

            if (!SecurityUtil.getId().equals(seatInfo.getUserId())) {
                throw new SeatException(ExceptionMessage.SEAT_NOT_SELECTED_BY_USER);
            }

            seatInfo.confirm(seatId);
            redisTemplateSeat.opsForHash().put(generateKey(performanceId), seatId.toString(), seatInfo);
        });
    }

    @Override
    public void cancel(List<UUID> seatIds, UUID performanceId) {

        seatIds.forEach(seatId -> {
            SeatInfoResponseDto seatInfo = getSeatFromRedis(performanceId, seatId);

            if (seatInfo == null) {
                throw new SeatException(ExceptionMessage.SEAT_NOT_FOUND);
            }

            if (!SecurityUtil.getId().equals(seatInfo.getUserId())) {
                throw new SeatException(ExceptionMessage.SEAT_NOT_SELECTED_BY_USER);
            }

            seatInfo.cancel();
            redisTemplateSeat.opsForHash().put(generateKey(performanceId), seatId.toString(), seatInfo);
        });
    }


    private String generateKey(UUID performanceId) {
        return "seats:" + performanceId;
    }

    private long getSelectedSeatsCount(UUID performanceId) {
        return getSeatsFromRedis(performanceId)
                .stream()
                .filter(seat -> SecurityUtil.getId().equals(seat.getUserId()))
                .count();
    }

    private int getTicketLimit(UUID performanceId) {
        return Integer.parseInt(
                (String) Objects.requireNonNull(
                        redisTemplateSeat.opsForHash()
                                .get(generateKey(performanceId), TICKET_LIMIT)
                ));
    }


}
