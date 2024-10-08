package com.ticketing.user.presentation.controller;

import com.ticketing.user.application.dto.request.GetNicknamesRequestDto;
import com.ticketing.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user-service/api/v1/users")
public class FeignUserController {

    private final UserService userService;

    // 닉네임 조회
    @PostMapping("/nickname")
    public ResponseEntity<?> nickname(@RequestBody GetNicknamesRequestDto requestDto) {

        return ResponseEntity.ok(userService.getNicknamesByUserIds(requestDto));
    }
}
