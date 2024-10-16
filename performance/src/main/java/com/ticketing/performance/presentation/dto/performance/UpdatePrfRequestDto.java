package com.ticketing.performance.presentation.dto.performance;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdatePrfRequestDto {

    @NotNull(message = "공연장은 필수입니다.")
    private UUID hallId;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 255, message = "제목은 255자 이하로 입력해야 합니다.")
    private String title;

    @NotBlank(message = "설명은 필수입니다.")
    @Size(max = 255, message = "설명은 255자 이하로 입력해야 합니다.")
    private String description;

    @Min(value = 1, message = "상연 시간은 1분 이상이어야 합니다.")
    private Integer runningTime;

    @Min(value = 0, message = "인터미션 시간은 0분 이상이어야 합니다.")
    private Integer intermission;

    @AgeLimitConstraint(message = "나이 제한은 0, 7, 12, 15, 19 중 하나여야 합니다.")
    private Integer ageLimit;

    @NotNull(message = "개막일은 필수입니다.")
    private LocalDate openDate;

    @NotNull(message = "공연 시간은 필수입니다.")

    private LocalDateTime performanceTime;

    @NotNull(message = "티켓 오픈 시간은 필수입니다.")
    private LocalDateTime ticketOpenTime;

    @Min(value = 1, message = "티켓 한도는 1장 이상이어야 합니다.")
    private Integer ticketLimit;


    @AssertTrue(message = "openDate는 티켓 오픈 시간 이전 이어야 합니다.")
    public boolean openDate() {
        return openDate.isBefore(ticketOpenTime.toLocalDate());
    }

    @AssertTrue(message = "티켓 오픈 시간 은 공연 시작 시간 이전 이어야 합니다.")
    public boolean ticketOpenTime() {
        return ticketOpenTime.isBefore(performanceTime);
    }
}
