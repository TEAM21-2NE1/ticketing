package com.ticketing.review.application.client.dto;

import com.ticketing.review.infrastructure.client.dto.GetNicknameResponseDto;

public record UserNicknameInfoDto(
    Long userId,
    String nickname
) {

  public static UserNicknameInfoDto toUserNicknameInfoDto(GetNicknameResponseDto userInfoDto) {
    return new UserNicknameInfoDto(userInfoDto.userId(), userInfoDto.nickname());
  }

}
