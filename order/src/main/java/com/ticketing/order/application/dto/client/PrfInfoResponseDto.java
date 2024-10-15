package com.ticketing.order.application.dto.client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class PrfInfoResponseDto {

    private UUID performanceId;
    private UUID hallId;
    private String hallName;
    private Long managerId;
    private String title;
    private String posterUrl;
    private String description;
    private Integer runningTime;
    private Integer intermission;
    private Integer ageLimit;
    private LocalDate openDate;
    private LocalDateTime performanceTime;
    private LocalDateTime ticketOpenTime;
    private Integer ticketLimit;
    private Integer totalSeat;
    private Integer availableSeat;
    private List<SeatInfo> seats;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatInfo {
        private UUID id;
        private Integer seatNum;
        private Integer seatRow;
        private String seatType;

    }
}