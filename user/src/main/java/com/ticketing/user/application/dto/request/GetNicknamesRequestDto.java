package com.ticketing.user.application.dto.request;

import java.util.List;

public record GetNicknamesRequestDto(
        List<Long> userIds
) {

}
