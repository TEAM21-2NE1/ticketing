package com.ticketing.review.infrastructure.client.dto;

import com.ticketing.review.application.client.dto.UserNicknameInfoDto;

public record GetNicknameResponseDto(

    Long userId,
    String nickname

) {

  public UserNicknameInfoDto toUserNicknameInfoDto() {
    return new UserNicknameInfoDto(this.userId(), this.nickname());
  }
}