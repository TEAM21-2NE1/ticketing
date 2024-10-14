package com.ticketing.review.infrastructure.client;

import com.ticketing.review.infrastructure.client.dto.GetNicknameResponseDto;
import com.ticketing.review.infrastructure.client.dto.GetNicknamesRequestDto;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient("user-service")
public interface UserClient {

  @PostMapping("/user-service/api/v1/users/nickname")
  ResponseEntity<List<GetNicknameResponseDto>> nickname(
      @RequestBody GetNicknamesRequestDto requestDto);
}
