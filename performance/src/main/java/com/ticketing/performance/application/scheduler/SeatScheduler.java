package com.ticketing.performance.application.scheduler;

import com.ticketing.performance.application.dto.performance.PrfRedisInfoDto;
import com.ticketing.performance.application.dto.seat.SeatInfoResponseDto;
import com.ticketing.performance.application.service.SeatOrderService;
import com.ticketing.performance.domain.model.Performance;
import com.ticketing.performance.domain.repository.PerformanceRepository;
import com.ticketing.performance.domain.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SeatScheduler {

    private final SeatRepository seatRepository;
    private final PerformanceRepository performanceRepository;
    private final SeatOrderService seatOrderService;

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

        performances.forEach(performance -> {
            List<SeatInfoResponseDto> seatsForPerformance = seatList.stream()
                    .filter(seat -> seat.getPerformanceId().equals(performance.getId()))
                    .toList();

            seatOrderService.saveSeatsToRedis(PrfRedisInfoDto.of(performance), seatsForPerformance);
        });
    }
}
