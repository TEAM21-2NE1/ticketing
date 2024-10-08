package com.ticketing.user.application.service;

import com.ticketing.user.application.dto.request.CreateUserRequestDto;
import com.ticketing.user.application.dto.request.GetNicknamesRequestDto;
import com.ticketing.user.application.dto.response.GetNicknameResponseDto;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    void signUp(CreateUserRequestDto requestDto);

    List<GetNicknameResponseDto> getNicknamesByUserIds(GetNicknamesRequestDto requestDto);
}
