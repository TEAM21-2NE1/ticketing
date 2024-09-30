package com.ticketing.user.application.dto.request;

import com.ticketing.user.domain.model.User;
import com.ticketing.user.domain.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record CreateUserRequestDto(

        @NotNull(message = "이메일은 필수 입력 값입니다.")
        @Size(min = 2, message = "이메일은 최소 2자 이상 작성해주세요.")
        @Email
        String email,

        @NotNull(message = "이름은 필수 입력 값입니다.")
        @Size(min = 1, message = "이름은 최소 1자 이상 작성해주세요.")
        String name,

        @NotNull(message = "비밀번호는 필수 입력 값입니다.")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상 작성해주세요.")
        String password,


        @NotNull(message = "생년월일은 필수 입력 값입니다.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate birthdate,

        @NotNull(message = "닉네임은 필수 입력 값입니다.")
        @Size(min = 1, message = "닉네임은 최소 1자 이상 작성해주세요.")
        String nickname,

        @NotNull(message = "권한은 필수 입력값입니다.")
        UserRole role
) {

    public static User toEntity(CreateUserRequestDto requestDto, String encodedPassword) {
        return User.builder()
                .email(requestDto.email)
                .name(requestDto.name)
                .password(encodedPassword)
                .nickname(requestDto.nickname)
                .role(requestDto.role)
                .build();
    }
}
