package com.ticketing.user.presentation.controller;

import static com.ticketing.user.common.response.SuccessResponse.success;

import com.ticketing.user.application.dto.request.CreateUserRequestDto;
import com.ticketing.user.application.service.UserService;
import com.ticketing.user.common.response.CommonResponse;
import com.ticketing.user.common.response.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/sign-up")
    public ResponseEntity<? extends CommonResponse> signUp(@RequestBody @Valid CreateUserRequestDto requestDto) {

        userService.signUp(requestDto);

        return ResponseEntity.status(SuccessCode.CREATE_SUCCESS.getHttpStatus())
                .body(success(SuccessCode.CREATE_SUCCESS.getMessage()));
    }
}
