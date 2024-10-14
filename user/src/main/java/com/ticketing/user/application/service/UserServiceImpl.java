package com.ticketing.user.application.service;

import com.ticketing.user.application.dto.request.CreateUserRequestDto;
import com.ticketing.user.application.dto.request.GetNicknamesRequestDto;
import com.ticketing.user.application.dto.response.GetNicknameResponseDto;
import com.ticketing.user.common.exception.UserException;
import com.ticketing.user.common.response.ErrorCode;
import com.ticketing.user.domain.model.User;
import com.ticketing.user.domain.model.UserDetailsImpl;
import com.ticketing.user.domain.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void signUp(CreateUserRequestDto requestDto) {

        // 1. 이메일 중복 체크
        if (userRepository.existsByEmailAndIsDeletedFalse(requestDto.email())) {
            throw new UserException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 2. 닉네임 중복 체크
        if (userRepository.existsByNicknameAndIsDeletedFalse(requestDto.nickname())) {
            throw new UserException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        // 3. 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(requestDto.password());
        User user = CreateUserRequestDto.toEntity(requestDto, encodedPassword);

        User savedUser = userRepository.save(user);

        // todo:: 2. 값 리턴
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(user.getRole().getAuthority()) // UserRole에서 authority 가져오기
        );

        return new UserDetailsImpl(
                user.getId(),              // userId를 UserDetailsImpl에 전달
                user.getEmail(),           // email (username) 전달
                user.getPassword(),        // password 전달
                authorities                // authorities 전달 (추가 가능)
        );
    }

    @Override
    public List<GetNicknameResponseDto> getNicknamesByUserIds(GetNicknamesRequestDto requestDto) {

        return userRepository.findNicknamesByUserIdsAndIsDeletedFalse(requestDto.userIds());
    }
}
