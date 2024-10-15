package com.ticketing.order.application.service;

import com.ticketing.order.application.dto.client.SeatInfoResponseDto;
import com.ticketing.order.application.dto.request.OrderSeatRequestDto;
import com.ticketing.order.application.dto.response.GetOrderPerformancesResponseDto;
import com.ticketing.order.application.dto.response.GetOrderResponseDto;
import com.ticketing.order.application.dto.response.GetOrderResponseDto.SeatDetail;
import com.ticketing.order.application.dto.response.GetOrderStatusResponse;
import com.ticketing.order.common.exception.OrderException;
import com.ticketing.order.common.response.ExceptionMessage;
import com.ticketing.order.config.SecurityUtil;
import com.ticketing.order.domain.model.Order;
import com.ticketing.order.domain.repository.OrderRepository;
import com.ticketing.order.infrastructure.PerformanceClient;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderRUDService {

    private final OrderRepository orderRepository;
    private final SeatOrderService seatOrderService;
    private final PerformanceClient performanceClient;

    @Transactional
    public void deleteOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(ExceptionMessage.ORDER_NOT_FOUND));

        order.delete(SecurityUtil.getId());
    }


    public GetOrderResponseDto getOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(ExceptionMessage.ORDER_NOT_FOUND));
        List<UUID> orderSelectedSeats = order.getSelectedSeatIds();

        List<SeatDetail> seats = performanceClient.orderSeats(
                SecurityUtil.getId().toString(),
                SecurityUtil.getRole(),
                SecurityUtil.getEmail(),
                new OrderSeatRequestDto(orderSelectedSeats)
        ).data().stream()
                .map(seat -> new SeatDetail(
                        seat.getSeatId(),
                        seat.getSeatNum(),
                        seat.getSeatRow(),
                        seat.getSeatType()))
                .toList();
//        List<SeatDetail> seats = orderSelectedSeats.stream()
//                .map(seatId -> seatOrderService.getSeatFromRedis(order.getPerformanceId(),
//                        seatId)) // Redis에서 좌석 정보 조회
//                .filter(Objects::nonNull) // 조회된 좌석 정보가 null이 아닌 경우에만 처리
//                .map(seatInfo -> new SeatDetail(
//                        seatInfo.getSeatId(),
//                        seatInfo.getSeatNum(),
//                        seatInfo.getSeatRow(),
//                        seatInfo.getSeatType()
//                )) // SeatDetail 객체로 변환
//                .toList();

        return GetOrderResponseDto.from(order, seats);
    }

    public GetOrderStatusResponse orderStatusResponse(String userId, UUID performanceId) {
        Order order = orderRepository.findByUserIdAndPerformanceId(userId, performanceId)
                .orElseThrow(() -> new OrderException(ExceptionMessage.ORDER_NOT_FOUND));

        return GetOrderStatusResponse.from(order);
    }

    public GetOrderPerformancesResponseDto getOrderPerformanceIds() {
        String userId = SecurityUtil.getId().toString();

        List<UUID> orderPerformanceIds = orderRepository.findPerformanceIdByUserIdAndIsDeletedIsFalse(
                userId);

        return GetOrderPerformancesResponseDto.builder()
                .performanceIds(orderPerformanceIds)
                .build();
    }
}
