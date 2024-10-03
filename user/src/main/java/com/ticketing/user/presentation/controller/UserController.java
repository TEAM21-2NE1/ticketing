package com.ticketing.user.presentation.controller;

import static com.ticketing.user.common.response.SuccessResponse.success;

import com.ticketing.user.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping("/test")
    public ResponseEntity<? extends CommonResponse> test() {

        return ResponseEntity.status(HttpStatus.OK)
                .body(success("테스트 완료!"));
    }
}
