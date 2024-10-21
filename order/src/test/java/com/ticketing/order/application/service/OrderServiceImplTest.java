package com.ticketing.order.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.ticketing.order.application.dto.request.CreateOrderRequestDto;
import com.ticketing.order.application.dto.response.CreateOrderResponseDto;
import com.ticketing.order.application.dto.response.CreateOrderResponseDto.SeatDetail;
import com.ticketing.order.common.exception.OrderException;
import com.ticketing.order.domain.model.User;
import com.ticketing.order.domain.model.WaitingQueue;
import com.ticketing.order.domain.repository.OrderRepository;
import com.ticketing.order.infrastructure.PerformanceClient;
import com.ticketing.order.infrastructure.RedisRunningQueue;
import com.ticketing.order.infrastructure.RedisWaitingQueue;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class OrderServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImplTest.class);

    @Autowired
    private RedisTemplate<String, User> redisTemplate; // RedisTemplate 주입

    private WaitingQueue waitingQueue;
    private OrderRepository orderRepository;
    private PerformanceClient performanceClient;
    private OrderService orderService;
    private SeatOrderService seatOrderService;

    @Autowired
    private RedisRunningQueue runningQueue;

    @BeforeEach
    public void setUp() {
        orderRepository = Mockito.mock(OrderRepository.class);
        performanceClient = Mockito.mock(PerformanceClient.class);
        waitingQueue = new RedisWaitingQueue(redisTemplate);

        orderService = new OrderServiceImpl(orderRepository, runningQueue, waitingQueue,
                performanceClient, redisTemplate, seatOrderService);

        // Clear the queues in Redis before each test
        waitingQueue.clear();
        runningQueue.clear();

        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    void 이미_접속한_유저는_예매를_할_수_있다() {
        // given
        UUID performanceId = UUID.randomUUID();
        UUID seatId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        CreateOrderResponseDto expected = new CreateOrderResponseDto(
                orderId,
                "1",
                performanceId,
                List.of(new SeatDetail(seatId, 12, 3, "VIP")),  // seatType 추가
                30000,
                "PENDING_PAYMENT",
                "CREDIT_CARD",
                0L
        );

        CreateOrderRequestDto createOrderRequestDto = new CreateOrderRequestDto(
                performanceId,
                List.of(seatId),
                "CREDIT_CARD"
        );

        Mockito.when(runningQueue.check(User.of("1"))).thenReturn(true);

        // when
        CreateOrderResponseDto actual = orderService.createOrder(createOrderRequestDto, "1",
                "ROLE_USER", "user@example.com");

        // then
        assertEquals(expected, actual);
    }

    @Test
    void 동시접속자가_최대허용이상이면_접속인원초과_오류가_발생한다() {
        // given
        UUID performanceId = UUID.randomUUID();
        UUID seatId = UUID.randomUUID();

        Mockito.when(runningQueue.available()).thenReturn(false);

        CreateOrderRequestDto createOrderRequestDto = new CreateOrderRequestDto(
                performanceId,
                List.of(seatId),
                "CREDIT_CARD"
        );

        // when
        Executable executable = () -> {
            orderService.createOrder(createOrderRequestDto, "2", "ROLE_USER", "user@example.com");
        };

        // then
        assertThrows(OrderException.class, executable);
    }


    // 대기열 10000명 test
    @Test
    void contextLoads() throws InterruptedException {
        int threadCount = 10000;
        int runningQueueLimit = 10; // RunningQueue 크기 제한 설정
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    CreateOrderRequestDto requestDto = new CreateOrderRequestDto(
                            UUID.fromString("655ee2b4-edb9-4bdb-aad2-594134f65702"),
                            List.of(UUID.randomUUID()), // 매번 고유한 좌석 ID 생성
                            "CREDIT_CARD"
                    );
                    orderService.createOrder(requestDto, String.valueOf(finalI), "ROLE_USER",
                            "user" + finalI + "@example.com");
                    log.info("test-i : {}", finalI);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        Awaitility.await()
                .atMost(20, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertEquals(runningQueueLimit, runningQueue.size(),
                            "RunningQueue should contain 10 users.");
                    assertEquals(threadCount - runningQueueLimit, waitingQueue.size(),
                            "WaitingQueue should contain remaining 10 users.");
                });
    }
}