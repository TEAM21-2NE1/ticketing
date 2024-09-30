package com.ticketing.performance.domain.model;

import com.ticketing.performance.common.auditor.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_performance")
@Getter
@Builder
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


}
