package com.ticketing.user.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginUserRequestDto (

        @NotNull(message = "이메일은 필수 입력 값입니다.")
        @Size(min = 2, message = "이메일은 최소 2자 이상 작성해주세요.")
        @Email
        String email,

        @NotNull(message = "비밀번호는 필수 입력 값입니다.")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상 작성해주세요.")
        String password
) {

}