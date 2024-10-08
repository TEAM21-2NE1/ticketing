package com.ticketing.performance.application.scheduler;

import com.ticketing.performance.application.dto.seat.SeatInfoResponseDto;
import com.ticketing.performance.application.service.OrderService;
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
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SeatScheduler {

    private final SeatRepository seatRepository;
    private final PerformanceRepository performanceRepository;
    private final OrderService orderService;

    @Scheduled(cron = "0 0 6 * * *", zone = "Asia/Seoul")
    public void run() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
        List<UUID> performanceIds = performanceRepository
                .findAllByTicketOpenTimeBetween(startOfDay, endOfDay)
                .stream()
                .map(Performance::getId)
                .toList();

        List<SeatInfoResponseDto> seatList = seatRepository.
                findAllByPerformanceIdIn(performanceIds)
                .stream()
                .map(SeatInfoResponseDto::of)
                .toList();

        orderService.seatUpdate(seatList);

    }
}
