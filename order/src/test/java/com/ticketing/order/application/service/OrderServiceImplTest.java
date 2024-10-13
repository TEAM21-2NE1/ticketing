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

    @Autowired
    private RedisRunningQueue runningQueue;

    @BeforeEach
    public void setUp() {
        orderRepository = Mockito.mock(OrderRepository.class);
        performanceClient = Mockito.mock(PerformanceClient.class);

        waitingQueue = new RedisWaitingQueue(redisTemplate);

        orderService = new OrderServiceImpl(orderRepository, runningQueue, waitingQueue,
                performanceClient);

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
                List.of(new SeatDetail(seatId, 12, 3)),
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
        CreateOrderResponseDto actual = orderService.createOrder(createOrderRequestDto, "1");

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
            orderService.createOrder(createOrderRequestDto, "2");
        };

        // then
        assertThrows(OrderException.class, executable);
    }

    @Test
    void contextLoads() throws InterruptedException {

        int threadCount = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        CreateOrderRequestDto requestDto = new CreateOrderRequestDto(
                UUID.fromString("655ee2b4-edb9-4bdb-aad2-594134f65702"),
                List.of(UUID.fromString("f3f0e1a6-0a19-4a3a-9d3d-53e223c1b9b7")),
                "CREDIT_CARD"
        );

        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    orderService.createOrder(requestDto, String.valueOf(finalI));
                    log.info("test-i : {}", finalI);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Thread.sleep(5000);  // 5초 대기

        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    long size = waitingQueue.size();
                    System.out.println("Current waiting queue size: " + size);
                    assertEquals(10, size);
                });
    }
}