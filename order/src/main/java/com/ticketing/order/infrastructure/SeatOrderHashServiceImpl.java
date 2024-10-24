package com.ticketing.order.infrastructure;


import com.ticketing.order.application.dto.PrfRedisInfoDto;
import com.ticketing.order.application.dto.client.SeatInfoResponseDto;
import com.ticketing.order.application.dto.response.GetSeatsResponseDto;
import com.ticketing.order.application.service.SeatOrderService;
import com.ticketing.order.common.exception.OrderException;
import com.ticketing.order.common.exception.SeatException;
import com.ticketing.order.common.response.ExceptionMessage;
import com.ticketing.order.config.SecurityUtil;
import com.ticketing.order.domain.model.User;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatOrderHashServiceImpl implements SeatOrderService {

    private static final String HOLD_SEAT_SET_KEY = "holdSeats:%s:%s";
    private static final String SEAT_TTL_KEY = "seatTTL:%s:%s:%s";
    private static final String SEATS_KEY = "seats:%s";
    private static final String LOCK_SEAT_KEY = "lock:seat:%s:%s";

    private final String TICKET_LIMIT = "ticketLimit";
    private final String TICKET_OPEN_TIME = "ticketOpenTime";

    private static final Integer EXPIRE_TIME_MINUTE = 600;

    private final RedisTemplate<String, Object> redisTemplateSeat;
    private final RedissonClient redissonClient;
    private final RedisRunningQueue runningQueue;
    private final RedisWaitingQueue waitingQueue;

    public void saveSeatsToRedis(PrfRedisInfoDto prfRedisInfoDto,
            List<SeatInfoResponseDto> seatList) {
        String key = String.format(SEATS_KEY, prfRedisInfoDto.getPerformanceId());
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
        Long userId = SecurityUtil.getId();
        String lockKey = String.format(LOCK_SEAT_KEY, performanceId.toString(), userId);
        RLock lock = redissonClient.getLock(lockKey);

        try {

            boolean isLocked = lock.tryLock(0, 1, TimeUnit.MINUTES);
            if (isLocked) {
                SeatInfoResponseDto seat = getSeatFromRedis(performanceId, seatId);

                validateSeatExists(seat);

                switch (seat.getSeatStatus()) {
                    case AVAILABLE -> {
                        checkTicketLimit(performanceId);

                        seat.hold();
                        updateSeatStatus(performanceId, seatId, seat);
                        addSeatToHoldSet(performanceId, seatId);
                        createSeatTTL(performanceId, seatId);
                    }
                    case HOLD -> {
                        checkSeatOwner(performanceId, seat);

                        seat.cancel();

                        updateSeatStatus(performanceId, seatId, seat);
                        removeSeatFromHoldSet(performanceId, seatId, userId);
                        removeSeatTTL(performanceId, seatId, userId);
                    }
                    case BOOKED -> throw new SeatException(ExceptionMessage.SEAT_ALREADY_BOOKED);
                }
            } else {
                throw new SeatException(ExceptionMessage.SEAT_ALREADY_HOLD);
            }


        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SeatException(ExceptionMessage.SEAT_ALREADY_HOLD);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private void removeSeatTTL(UUID performanceId, UUID seatId, Long userId) {
        redisTemplateSeat.delete(String.format(SEAT_TTL_KEY, performanceId, seatId, userId));
    }

    private void createSeatTTL(UUID performanceId, UUID seatId) {
        redisTemplateSeat.opsForValue().set(
                String.format(SEAT_TTL_KEY, performanceId, seatId, SecurityUtil.getId()),
                "",
                EXPIRE_TIME_MINUTE,
                TimeUnit.SECONDS
        );
    }

    private void addSeatToHoldSet(UUID performanceId, UUID seatId) {
        String key = String.format(HOLD_SEAT_SET_KEY, performanceId.toString(),
                SecurityUtil.getId());
        redisTemplateSeat.opsForSet().add(key, seatId.toString());
    }

    private void removeSeatFromHoldSet(UUID performanceId, UUID seatId, Long userId) {
        String key = String.format(HOLD_SEAT_SET_KEY, performanceId, userId);
        redisTemplateSeat.opsForSet().remove(key, seatId);
    }


    @Override
    public SeatInfoResponseDto getSeatFromRedis(UUID performanceId, UUID seatId) {
        Object seat = redisTemplateSeat.opsForHash()
                .get(String.format(SEATS_KEY, performanceId), seatId.toString());
        return seat != null ? (SeatInfoResponseDto) seat : null;
    }

    public GetSeatsResponseDto getSeats(UUID performanceId) {
        Long userId = SecurityUtil.getId();
        User user = User.of(String.valueOf(userId));

        if (!runningQueue.check(user)) {
            try {
                // 실행 큐에 넣기 시도
                runningQueue.push(user);
            } catch (OrderException e) {
                // 실행 큐가 가득 찬 경우 대기열에 등록
                waitingQueue.register(user);
                var waitingTicket = waitingQueue.getTicket(user);
                var totalWaiting = waitingQueue.size();
                return GetSeatsResponseDto.waiting(waitingTicket, totalWaiting);
            }
        }

        String key = String.format(SEATS_KEY, performanceId);
        String openTimeStr = (String) redisTemplateSeat.opsForHash().get(key, TICKET_OPEN_TIME);

        if (openTimeStr == null || LocalDateTime.now().isBefore(LocalDateTime.parse(openTimeStr))) {
            throw new SeatException(ExceptionMessage.TICKET_NOT_OPEN);
        }

        Map<Object, Object> seatMap = redisTemplateSeat.opsForHash().entries(key);
        seatMap.remove(TICKET_LIMIT);
        seatMap.remove(TICKET_OPEN_TIME);

        List<SeatInfoResponseDto> seats = seatMap.values().parallelStream()
                .map(value -> (SeatInfoResponseDto) value)
                .toList();

        return GetSeatsResponseDto.success(seats);
    }

    public List<SeatInfoResponseDto> getSeatsFromRedis(UUID performanceId) {

        String key = String.format(SEATS_KEY, performanceId);
        String openTimeStr = (String) redisTemplateSeat.opsForHash().get(key, TICKET_OPEN_TIME);

        if (openTimeStr == null || LocalDateTime.now().isBefore(LocalDateTime.parse(openTimeStr))) {
            throw new SeatException(ExceptionMessage.TICKET_NOT_OPEN);
        }

        Map<Object, Object> seatMap = redisTemplateSeat.opsForHash().entries(key);
        seatMap.remove(TICKET_LIMIT);
        seatMap.remove(TICKET_OPEN_TIME);

        return seatMap.values().parallelStream()
                .map(value -> (SeatInfoResponseDto) value)
                .toList();
    }


    @Override
    public void confirm(List<UUID> seatIds, UUID performanceId) {
        Long userId = SecurityUtil.getId();
        seatIds.forEach(seatId -> {
            SeatInfoResponseDto seatInfo = getSeatFromRedis(performanceId, seatId);

            validateSeatExists(seatInfo);

            if (!SecurityUtil.getId().equals(seatInfo.getUserId())) {
                throw new SeatException(ExceptionMessage.SEAT_NOT_SELECTED_BY_USER);
            }

            seatInfo.confirm(seatId);
            updateSeatStatus(performanceId, seatId, seatInfo);
            removeSeatFromHoldSet(performanceId, seatId, userId);
            removeSeatTTL(performanceId, seatId, userId);
        });
    }

    @Override
    public void cancel(List<UUID> seatIds, UUID performanceId) {

        seatIds.forEach(seatId -> {
            SeatInfoResponseDto seatInfo = getSeatFromRedis(performanceId, seatId);

            validateSeatExists(seatInfo);

            if (!SecurityUtil.getId().equals(seatInfo.getUserId())) {
                throw new SeatException(ExceptionMessage.SEAT_NOT_SELECTED_BY_USER);
            }

            seatInfo.cancel();
            updateSeatStatus(performanceId, seatId, seatInfo);
        });
    }

    private void validateSeatExists(SeatInfoResponseDto seat) {
        if (seat == null) {
            throw new SeatException(ExceptionMessage.SEAT_NOT_FOUND);
        }
    }

    public void releaseExpiredSeats(UUID performanceId, UUID seatId, SeatInfoResponseDto seat,
            Long userId) {
        updateSeatStatus(performanceId, seatId, seat);
        removeSeatFromHoldSet(performanceId, seatId, userId);
    }

    private void updateSeatStatus(UUID performanceId, UUID seatId, SeatInfoResponseDto seat) {
        redisTemplateSeat.opsForHash()
                .put(String.format(SEATS_KEY, performanceId), seatId.toString(), seat);
    }

    private void checkSeatOwner(UUID performanceId, SeatInfoResponseDto seat) {
        if (!seat.getUserId().equals(SecurityUtil.getId())) {
            throw new SeatException(ExceptionMessage.SEAT_ALREADY_HOLD,
                    getSeatsFromRedis(performanceId));
        }
    }

    private void checkTicketLimit(UUID performanceId) {
        if (getSelectedSeatsCount(performanceId) >= getTicketLimit(performanceId)) {
            throw new SeatException(ExceptionMessage.SEAT_SELECT_EXCEEDED);
        }
    }


    private Long getSelectedSeatsCount(UUID performanceId) {
        String key = String.format(HOLD_SEAT_SET_KEY, performanceId.toString(),
                SecurityUtil.getId());
        return redisTemplateSeat.opsForSet().size(key);
    }

    private int getTicketLimit(UUID performanceId) {
        return Integer.parseInt(
                (String) Objects.requireNonNull(
                        redisTemplateSeat.opsForHash()
                                .get(String.format(SEATS_KEY, performanceId), TICKET_LIMIT)
                ));
    }

    public void deleteOrderSeats(UUID performanceId) {
        redisTemplateSeat.unlink(String.format(SEATS_KEY, performanceId.toString()));
    }

    @Scheduled(fixedRate = 100)
    public void transferWaitingToRunning() {
        try {
            while (runningQueue.available()) {
                User user = waitingQueue.pop();
                if (user == null) {
                    break;
                }
                try {
                    runningQueue.push(user);
                } catch (OrderException e) {
                    waitingQueue.register(user);
                    break;
                }
            }
        } catch (Exception e) {
            log.error("Error in transferWaitingToRunning: ", e);
        }
    }

}
