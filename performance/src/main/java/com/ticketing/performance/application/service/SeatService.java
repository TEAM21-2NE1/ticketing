package com.ticketing.performance.application.service;

import com.ticketing.performance.application.dto.hall.HallInfoResponseDto;
import com.ticketing.performance.application.dto.hall.HallSeatInfoResponseDto;
import com.ticketing.performance.application.dto.performance.PrfRedisInfoDto;
import com.ticketing.performance.application.dto.seat.SeatInfoResponseDto;
import com.ticketing.performance.common.exception.ForbiddenAccessException;
import com.ticketing.performance.common.exception.PerformanceException;
import com.ticketing.performance.common.exception.SeatException;
import com.ticketing.performance.common.response.ErrorCode;
import com.ticketing.performance.common.util.SecurityUtil;
import com.ticketing.performance.domain.model.Performance;
import com.ticketing.performance.domain.model.Seat;
import com.ticketing.performance.domain.repository.PerformanceRepository;
import com.ticketing.performance.domain.repository.SeatRepository;
import com.ticketing.performance.presentation.dto.seat.CreateSeatRequestDto;
import com.ticketing.performance.presentation.dto.seat.UpdateSeatPriceRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatService {

    private final SeatRepository seatRepository;
    private final HallService hallService;
    private final PerformanceRepository performanceRepository;
    private final SeatOrderService seatOrderService;

    public List<SeatInfoResponseDto> getSeats(UUID performanceId) {
        return seatRepository.findAllByPerformanceId(performanceId)
                .stream()
                .map(SeatInfoResponseDto::of)
                .toList();
    }

    public List<SeatInfoResponseDto> getOrderSeats(UUID performanceId) {
        List<SeatInfoResponseDto> seatsFromRedis = seatOrderService.getSeatsFromRedis(performanceId);
        if (!seatsFromRedis.isEmpty()) {
            return seatsFromRedis;
        }
        PrfRedisInfoDto prfRedisInfoDto = performanceRepository.findById(performanceId).map(PrfRedisInfoDto::of)
                .orElseThrow(() -> new PerformanceException(ErrorCode.PERFORMANCE_NOT_FOUND));

        if (prfRedisInfoDto.getTicketOpenTime().isAfter(LocalDateTime.now())
                || (prfRedisInfoDto.getPerformanceTime().plusDays(1)).isBefore(LocalDateTime.now())) {
            throw new SeatException(ErrorCode.SEAT_QUERY_PERIOD_INVALID);
        }

        List<Seat> seats = seatRepository.findAllByPerformanceId(performanceId);
        List<SeatInfoResponseDto> seatList = seats.stream()
                .map(SeatInfoResponseDto::of)
                .toList();

        seatOrderService.saveSeatsToRedis(prfRedisInfoDto, seatList);

        return seatList;
    }


    @Transactional
    public void deleteSeatsByPerformanceId(UUID performanceId) {
        seatRepository.softDeleteSeatsByPerformanceId(performanceId);
    }

    @Transactional
    public void createSeat(CreateSeatRequestDto requestDto) {

        UUID performanceId = requestDto.getPerformanceId();

        if (seatRepository.existsByPerformanceId(performanceId)) {
            throw new SeatException(ErrorCode.SEAT_ALREADY_EXISTS);
        }

        HallInfoResponseDto hall = hallService.getHall(requestDto.getHallId());
        List<HallSeatInfoResponseDto> hallSeats = hall.getSeats();

        List<Seat> seats = new ArrayList<>();
        for (HallSeatInfoResponseDto hallSeat : hallSeats) {
            //todo : hall 좌석 구역과 요청 구역 검증
            Integer price = requestDto.getSections()
                    .stream()
                    .filter(s -> hallSeat.getSeatType().equals(s.getSeatType()))
                    .findFirst()
                    .orElseThrow(() -> new SeatException(ErrorCode.SEAT_TYPE_NOT_EXIST))
                    .getPrice();


            for (int seatRows = 1; seatRows <= hallSeat.getRows(); seatRows++) {
                for (int seatNum = 1; seatNum <= hallSeat.getSeatsPerRow(); seatNum++) {
                    Seat seat = Seat.create(performanceId, hallSeat.getSeatType(), seatRows, seatNum, price);
                    seats.add(seat);
                }
            }
        }
        seatRepository.saveAll(seats);
    }

    @Transactional
    public void updateSeatPrice(UpdateSeatPriceRequestDto requestDto) {
        Performance performance = performanceRepository.findById(requestDto.getPerformanceId())
                .orElseThrow(() -> new PerformanceException(ErrorCode.PERFORMANCE_NOT_FOUND));

        checkRole(performance.getManagerId());

        seatRepository.updateSeatPriceBySeatType(
                requestDto.getSeatType(),
                requestDto.getPrice(),
                requestDto.getPerformanceId()
        );
    }

    private void checkRole(Long managerId) {
        Long userId = SecurityUtil.getId();
        String role = SecurityUtil.getRole();

        if (role.equals("ROLE_P_MANAGER")) {
            if (!userId.equals(managerId)) {
                throw new ForbiddenAccessException(ErrorCode.FORBIDDEN_ACCESS);
            }
        }
    }

}
