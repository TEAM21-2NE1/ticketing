package com.ticketing.performance.domain.model;

import com.ticketing.performance.common.auditor.BaseEntity;
import com.ticketing.performance.presentation.dto.performance.CreatePrfRequestDto;
import com.ticketing.performance.presentation.dto.performance.UpdatePrfRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_performance")
@Getter
@Builder
@SQLRestriction("is_deleted = false")
public class Performance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID hallId;

    @Comment(value = "공연 관리자 ID")
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

    @Comment(value = "인당 최대 예매 가능 티켓 수")
    private Integer ticketLimit;

    public static Performance create(CreatePrfRequestDto requestDto,Long userId, String posterUrl) {
        return Performance.builder()
                .hallId(requestDto.getHallId())
                .managerId(userId)
                .title(requestDto.getTitle())
                .posterUrl(posterUrl)
                .description(requestDto.getDescription())
                .runningTime(requestDto.getRunningTime())
                .intermission(requestDto.getIntermission())
                .ageLimit(requestDto.getAgeLimit())
                .openDate(requestDto.getOpenDate())
                .performanceTime(requestDto.getPerformanceTime())
                .ticketOpenTime(requestDto.getTicketOpenTime())
                .ticketLimit(requestDto.getTicketLimit())
                .build();
    }


    public void update(UpdatePrfRequestDto requestDto) {
        this.hallId = requestDto.getHallId();
        this.title = requestDto.getTitle();
        this.description = requestDto.getDescription();
        this.runningTime = requestDto.getRunningTime();
        this.intermission = requestDto.getIntermission();
        this.ageLimit = requestDto.getAgeLimit();
        this.openDate = requestDto.getOpenDate();
        this.performanceTime = requestDto.getPerformanceTime();
        this.ticketOpenTime = requestDto.getTicketOpenTime();
        this.ticketLimit = requestDto.getTicketLimit();
    }

    public void delete() {
        super.delete();
    }

    public void rollbackDelete() {
        super.rollbackDelete();
    }
}
