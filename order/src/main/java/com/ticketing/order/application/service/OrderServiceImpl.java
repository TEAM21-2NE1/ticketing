package com.ticketing.order.application.service;

import com.ticketing.order.application.dto.client.SeatInfoResponseDto;
import com.ticketing.order.application.dto.client.SeatStatus;
import com.ticketing.order.application.dto.request.CreateOrderRequestDto;
import com.ticketing.order.application.dto.response.CreateOrderResponseDto;
import com.ticketing.order.application.dto.response.CreateOrderResponseDto.SeatDetail;
import com.ticketing.order.common.exception.SeatException;
import com.ticketing.order.common.response.ExceptionMessage;
import com.ticketing.order.domain.model.Order;
import com.ticketing.order.domain.model.User;
import com.ticketing.order.domain.model.WaitingQueue;
import com.ticketing.order.domain.model.WaitingTicket;
import com.ticketing.order.domain.repository.OrderRepository;
import com.ticketing.order.infrastructure.PerformanceClient;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PerformanceClient performanceClient;
    private final SeatOrderService seatOrderService;
    private final WaitingQueue waitingQueue;

    // 주문 생성
    @Transactional
    public CreateOrderResponseDto createOrder(CreateOrderRequestDto requestDto, String userId,
            String userRole, String userEmail) {

        // 공연 정보
        performanceClient.getPerformance(userId, userRole, userEmail,
                requestDto.performanceId());
        Order order = orderRepository.findBySelectedSeatIds(requestDto.selectedSeatIds(), userId);

        if (order != null) {
            return handleExistingOrder(requestDto, userId, order);
        }

        validateSeats(requestDto.performanceId(), requestDto.selectedSeatIds(), userId);

        // 주문 생성 및 처리
        Integer totalAmount = getTotalAmount(requestDto.performanceId(),
                requestDto.selectedSeatIds());
        Order newOrder = Order.of(requestDto, userId, totalAmount);

        orderRepository.save(newOrder);

        List<SeatDetail> seatDetails = createSeatDetails(requestDto.performanceId(),
                requestDto.selectedSeatIds());
        return CreateOrderResponseDto.from(newOrder, seatDetails);

    }

    public WaitingTicket getTicket(String userId) {
        return waitingQueue.getTicket(User.of(userId));
    }

    // 기존 주문이 있는 경우 처리
    private CreateOrderResponseDto handleExistingOrder(CreateOrderRequestDto requestDto,
            String userId, Order existingOrder) {
        if (!existingOrder.getUserId().equals(userId)) {
            throw new SeatException(ExceptionMessage.SEAT_NOT_SELECTED_BY_USER);
        }

        validateSeats(requestDto.performanceId(), requestDto.selectedSeatIds(), userId);
        return returnExistingOrderDetails(requestDto, existingOrder);
    }

    // 기존 주문의 상세 정보 반환
    private CreateOrderResponseDto returnExistingOrderDetails(CreateOrderRequestDto requestDto,
            Order order) {
        List<SeatDetail> seatDetails = createSeatDetails(requestDto.performanceId(),
                requestDto.selectedSeatIds());
        return CreateOrderResponseDto.from(order, seatDetails);
    }

    // 좌석 상태 검증
    private void validateSeats(UUID performanceId, List<UUID> seatIds, String userId) {
        for (UUID seatId : seatIds) {
            SeatInfoResponseDto seatInfo = seatOrderService.getSeatFromRedis(performanceId, seatId);

            if (seatInfo == null) {
                throw new SeatException(ExceptionMessage.SEAT_NOT_FOUND);
            }

            if (seatInfo.getSeatStatus() == SeatStatus.BOOKED) {
                throw new SeatException(ExceptionMessage.SEAT_ALREADY_BOOKED);
            }

            validateSeatHoldStatus(seatInfo, userId);
        }
    }

    // HOLD 상태와 사용자 일치 여부 검증
    private void validateSeatHoldStatus(SeatInfoResponseDto seatInfo, String userId) {
        boolean isStatusMatch = seatInfo.getSeatStatus() == SeatStatus.HOLD;
        boolean isUserMatch = Long.parseLong(userId) == seatInfo.getUserId();

        if (!isStatusMatch || !isUserMatch) {
            throw new SeatException(ExceptionMessage.SEAT_NOT_SELECTED_BY_USER);
        }
    }

    private Integer getTotalAmount(UUID performanceId, List<UUID> seatIds) {
        return seatIds.stream().map(
                        seatId -> {
                            SeatInfoResponseDto seatInfo = seatOrderService.getSeatFromRedis(performanceId,
                                    seatId);

                            if (seatInfo.getSeatStatus().equals(SeatStatus.BOOKED)) {
                                throw new SeatException(ExceptionMessage.SEAT_ALREADY_BOOKED);
                            }

                            return seatInfo.getPrice();
                        })
                .reduce(0, Integer::sum);
    }

    // 좌석 상세 정보 생성
    private List<SeatDetail> createSeatDetails(UUID performanceId, List<UUID> seatIds) {
        return seatIds.stream().map(
                seatId -> {
                    SeatInfoResponseDto seatInfo = seatOrderService.getSeatFromRedis(performanceId,
                            seatId);
                    return new SeatDetail(seatInfo.getSeatId(),
                            seatInfo.getSeatNum(),
                            seatInfo.getSeatRow(),
                            seatInfo.getSeatType()
                    );
                }).toList();
    }


}