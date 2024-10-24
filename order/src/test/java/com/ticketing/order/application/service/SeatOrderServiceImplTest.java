package com.ticketing.order.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ticketing.order.application.dto.PrfRedisInfoDto;
import com.ticketing.order.application.dto.client.SeatInfoResponseDto;
import com.ticketing.order.application.dto.client.SeatStatus;
import com.ticketing.order.application.dto.response.GetSeatsResponseDto;
import com.ticketing.order.domain.model.RunningQueue;
import com.ticketing.order.domain.model.WaitingQueue;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest
class SeatOrderServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(SeatOrderServiceImplTest.class);

    @Autowired
    private SeatOrderService seatOrderService;

    @Autowired
    private RunningQueue runningQueue;

    @Autowired
    private WaitingQueue waitingQueue;

    @BeforeEach
    public void setUp() {
        runningQueue.clear();
        waitingQueue.clear();
    }

    @Test
    void 대기열_10000명_테스트() throws InterruptedException {

        // given
        int threadCount = 10000;
        int runningQueueLimit = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);

        // 테스트용 공연 정보 및 좌석 정보 생성
        UUID performanceId = UUID.fromString("655ee2b4-edb9-4bdb-aad2-594134f65702");

        // Redis에 테스트용 좌석 데이터 저장
        PrfRedisInfoDto prfRedisInfoDto = PrfRedisInfoDto.builder()
                .performanceId(performanceId)
                .ticketLimit(4)
                .ticketOpenTime(LocalDateTime.now().minusMinutes(1))
                .performanceTime(LocalDateTime.now().plusDays(7))
                .build();

        // 좌석 생성
        List<SeatInfoResponseDto> seatList = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            seatList.add(SeatInfoResponseDto.builder()
                    .seatId(UUID.randomUUID())
                    .performanceId(performanceId)
                    .seatType("VIP")
                    .seatRow(1)
                    .seatNum(i)
                    .price(100000)
                    .seatStatus(SeatStatus.AVAILABLE)
                    .build());
        }

        // Redis에 좌석 정보 저장
        seatOrderService.saveSeatsToRedis(prfRedisInfoDto, seatList);

        List<UUID> seatIds = seatList.stream()
                .map(SeatInfoResponseDto::getSeatId)
                .collect(Collectors.toList());

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger waitingCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    startLatch.await();  // 모든 스레드가 준비될 때까지 대기

                    String userId = String.valueOf(finalI);
                    String email = "user" + finalI + "@test.com";

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    "password",
                                    List.of(() -> "ROLE_USER")
                            );
                    authentication.setDetails(userId);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    UUID seatId = seatIds.get(finalI);
                    try {
                        GetSeatsResponseDto response = seatOrderService.getSeats(performanceId);

                        if ("SUCCESS".equals(response.status())) {
                            successCount.incrementAndGet();
                            List<UUID> seatIdList = List.of(seatId);
                            seatOrderService.confirm(seatIdList, performanceId);
                        } else if ("WAITING".equals(response.status())) {
                            waitingCount.incrementAndGet();
                        }

                        log.info("User {} processed. Status: {}", finalI, response.status());
                    } catch (Exception e) {
                        log.error("Error for user {}: {}", finalI, e.getMessage());
                    }
                } catch (Exception e) {
                    log.error("Thread error for user {}: {}", finalI, e.getMessage());
                } finally {
                    SecurityContextHolder.clearContext();
                    latch.countDown();
                }
            });
        }

        startLatch.countDown();
        latch.await(30, TimeUnit.SECONDS);
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        Thread.sleep(2000);

        // then
        Long finalRunningQueueSize = runningQueue.size();
        Long finalWaitingQueueSize = waitingQueue.size();

        log.info("Final Queue Status - Running: {}, Waiting: {}", finalRunningQueueSize,
                finalWaitingQueueSize);
        log.info("Response Status Counts - Success: {}, Waiting: {}", successCount.get(),
                waitingCount.get());

        assertEquals(runningQueueLimit, finalRunningQueueSize,
                "RunningQueue size should be exactly " + runningQueueLimit);
        assertEquals(threadCount - runningQueueLimit, finalWaitingQueueSize,
                "WaitingQueue should contain " + (threadCount - runningQueueLimit) + " users");
    }
}