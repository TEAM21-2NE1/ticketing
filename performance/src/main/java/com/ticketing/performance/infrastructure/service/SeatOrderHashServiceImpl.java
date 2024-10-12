package com.ticketing.performance.infrastructure.service;

import com.ticketing.performance.application.dto.performance.PrfRedisInfoDto;
import com.ticketing.performance.application.dto.seat.SeatInfoResponseDto;
import com.ticketing.performance.application.service.SeatOrderService;
import com.ticketing.performance.common.exception.SeatException;
import com.ticketing.performance.common.response.ErrorCode;
import com.ticketing.performance.common.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatOrderHashServiceImpl implements SeatOrderService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final String TICKET_LIMIT = "ticketLimit";
    private final String TICKET_OPEN_TIME = "ticketOpenTime";

    public void saveSeatsToRedis(PrfRedisInfoDto prfRedisInfoDto, List<SeatInfoResponseDto> seatList) {
        String key = generateKey(prfRedisInfoDto.getPerformanceId());
        Map<String, SeatInfoResponseDto> seatMap = seatList.stream()
                .collect(Collectors.toMap(
                        seat -> seat.getSeatId().toString(),
                        seat -> seat
                ));
        redisTemplate.opsForHash().putAll(key, seatMap);
        redisTemplate.opsForHash().put(key, TICKET_LIMIT, String.valueOf(prfRedisInfoDto.getTicketLimit()));
        redisTemplate.opsForHash().put(key, TICKET_OPEN_TIME, prfRedisInfoDto.getTicketOpenTime().toString());

        Duration ttlDuration = Duration.between(LocalDateTime.now(), prfRedisInfoDto.getPerformanceTime().plusDays(1));

        if (ttlDuration.isNegative() || ttlDuration.isZero()) {
            redisTemplate.delete(key);
        }

        redisTemplate.expire(key, ttlDuration);

    }

    public void holdSeat(UUID performanceId, UUID seatId) {

        SeatInfoResponseDto seat = (SeatInfoResponseDto) redisTemplate.opsForHash().get(generateKey(performanceId), seatId.toString());
        if (seat == null) {
            throw new SeatException(ErrorCode.SEAT_NOT_FOUND);
        }

        switch (seat.getSeatStatus()) {
            case AVAILABLE -> {
                if (getSelectedSeatsCount(performanceId) >= getTicketLimit(performanceId)) {
                    throw new SeatException(ErrorCode.SEAT_SELECT_EXCEEDED);
                }
                seat.hold();
                redisTemplate.opsForHash().put(generateKey(performanceId), seatId.toString(), seat);
            }
            case HOLD -> {
                if (!seat.getUserId().equals(SecurityUtil.getId())) {
                    throw new SeatException(ErrorCode.SEAT_ALREADY_HOLD, getSeatsFromRedis(performanceId));
                }
                seat.cancel();
                redisTemplate.opsForHash().put(generateKey(performanceId), seatId.toString(), seat);
            }
            case BOOKED -> throw new SeatException(ErrorCode.SEAT_ALREADY_BOOKED, getSeatsFromRedis(performanceId));

            default -> throw new SeatException(ErrorCode.SEAT_STATUS_NOT_EXIST);
        }
    }

    public SeatInfoResponseDto getSeatFromRedis(UUID performanceId, UUID seatId) {
        Object seat = redisTemplate.opsForHash().get(generateKey(performanceId), seatId.toString());
        return seat != null ? (SeatInfoResponseDto) seat : null;
    }

    public List<SeatInfoResponseDto> getSeatsFromRedis(UUID performanceId) {
        String key = generateKey(performanceId);
        String openTimeStr = (String) redisTemplate.opsForHash().get(key, TICKET_OPEN_TIME);

        if (openTimeStr != null && LocalDateTime.now().isBefore(LocalDateTime.parse(openTimeStr))) {
            throw new SeatException(ErrorCode.TICKET_NOT_OPEN);
        }
        Map<Object, Object> seatMap = redisTemplate.opsForHash().entries(key);
        seatMap.remove(TICKET_LIMIT);
        seatMap.remove(TICKET_OPEN_TIME);
        return seatMap.values().stream()
                .map(value -> (SeatInfoResponseDto) value)
                .toList();
    }

    @Override
    public void confirm(List<SeatInfoResponseDto> seats) {
        seats.forEach(seat -> {
            SeatInfoResponseDto seatInfo = (SeatInfoResponseDto) redisTemplate.opsForHash()
                    .get(generateKey(seat.getPerformanceId())
                            , seat.getSeatId().toString());

            if (seatInfo == null) {
                throw new SeatException(ErrorCode.SEAT_NOT_FOUND);
            }

            seatInfo.confirm(seat.getOrderId());
            redisTemplate.opsForHash().put(generateKey(seat.getPerformanceId()), seat.getSeatId().toString(), seatInfo);
        });
    }

    @Override
    public void cancel(List<SeatInfoResponseDto> seats) {

        seats.forEach(seat -> {
            SeatInfoResponseDto seatInfo = (SeatInfoResponseDto) redisTemplate.opsForHash()
                    .get(generateKey(seat.getPerformanceId())
                            , seat.getSeatId().toString());

            if (seatInfo == null) {
                throw new SeatException(ErrorCode.SEAT_NOT_FOUND);
            }

            seatInfo.cancel();
            redisTemplate.opsForHash().put(generateKey(seat.getPerformanceId()), seat.getSeatId().toString(), seatInfo);
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
                        redisTemplate.opsForHash()
                                .get(generateKey(performanceId), TICKET_LIMIT)
                ));
    }

}
