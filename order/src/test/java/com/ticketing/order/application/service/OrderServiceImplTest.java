package com.ticketing.order.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.ticketing.order.application.dto.request.CreateOrderRequestDto;
import com.ticketing.order.application.dto.response.CreateOrderResponseDto;
import com.ticketing.order.application.dto.response.CreateOrderResponseDto.SeatDetail;
import com.ticketing.order.common.exception.OrderException;
import com.ticketing.order.domain.model.RunningQueue;
import com.ticketing.order.domain.model.User;
import com.ticketing.order.domain.model.WaitingQueue;
import com.ticketing.order.domain.repository.OrderRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;

class OrderServiceImplTest {

    OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
    RunningQueue runningQueue = Mockito.mock(RunningQueue.class);
    WaitingQueue waitingQueue = Mockito.mock(WaitingQueue.class);
    OrderService orderService = new OrderServiceImpl(orderRepository, runningQueue, waitingQueue);

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
                List.of(
                        new SeatDetail(seatId, 12, 3)
                ),
                30000,
                "PENDING_PAYMENT",
                "CREDIT_CARD"

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
    public void 동시접속자가_최대허용이상이면_접속인원초과_오류가_발생한다() {
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

}