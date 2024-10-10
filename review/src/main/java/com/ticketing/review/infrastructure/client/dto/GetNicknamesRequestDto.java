package com.ticketing.review.infrastructure.client.dto;

import java.util.List;

public record GetNicknamesRequestDto(
    List<Long> userIds
) {

  public static GetNicknamesRequestDto toGetNicknamesRequestDto(List<Long> userIds) {
    return new GetNicknamesRequestDto(userIds);
  }
}