package com.ticketing.performance.application.scheduler;

import com.ticketing.performance.application.dto.performance.PrfRedisInfoDto;
import com.ticketing.performance.application.dto.seat.SeatInfoResponseDto;
import com.ticketing.performance.application.service.OrderService;
import com.ticketing.performance.common.util.SecurityUtil;
import com.ticketing.performance.domain.model.Performance;
import com.ticketing.performance.domain.repository.PerformanceRepository;
import com.ticketing.performance.domain.repository.SeatRepository;
import com.ticketing.performance.infrastructure.client.OrderSeatInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeatScheduler {

    private final SeatRepository seatRepository;
    private final PerformanceRepository performanceRepository;
    private final OrderService orderService;

    @Scheduled(cron = "0 0 6 * * *", zone = "Asia/Seoul")
    public void run() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        List<Performance> performances = performanceRepository
                .findAllByTicketOpenTimeBetween(startOfDay, endOfDay);

        List<SeatInfoResponseDto> seatList = seatRepository.
                findAllByPerformanceIdIn(performances.stream().map(Performance::getId).toList())
                .stream()
                .map(SeatInfoResponseDto::of)
                .toList();

        log.info("{}", performances.size());
        log.info("{}", seatList.size());
        performances.forEach(performance -> {
            List<SeatInfoResponseDto> seatsForPerformance = seatList.stream()
                    .filter(seat -> seat.getPerformanceId().equals(performance.getId()))
                    .toList();

            orderService.insertSeats(SecurityUtil.getId().toString(), SecurityUtil.getRole(), SecurityUtil.getEmail(),
                    new OrderSeatInfoDto(PrfRedisInfoDto.of(performance), seatsForPerformance));

        });
    }
}
