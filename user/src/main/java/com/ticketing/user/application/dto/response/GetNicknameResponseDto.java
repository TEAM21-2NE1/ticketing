package com.ticketing.user.application.dto.response;

import com.ticketing.user.domain.model.User;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record GetNicknameResponseDto(

        Long userId,
        String nickname

) {

    public static GetNicknameResponseDto toDto(User user) {
        return GetNicknameResponseDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .build();
    }
}
