package com.ticketing.user.application.service;

import com.ticketing.user.application.dto.request.CreateUserRequestDto;
import com.ticketing.user.application.dto.request.GetNicknamesRequestDto;
import java.util.Map;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

  void signUp(CreateUserRequestDto requestDto);

  Map<Long, String> getNicknamesByUserIds(GetNicknamesRequestDto requestDto);
}
