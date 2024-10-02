package com.ticketing.user.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.ticketing.user.common.exception.UserException;
import com.ticketing.user.common.response.ErrorCode;
import lombok.Getter;

@Getter
public enum UserRole {

    USER(Authority.USER),           // 일반 사용자
    P_MANAGER(Authority.P_MANAGER), // 공연 관리자
    MANAGER(Authority.MANAGER);     // 관리자

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String P_MANAGER = "ROLE_P_MANAGER";
        public static final String MANAGER = "ROLE_MANAGER";
    }

    // String에서 UserRole로 변환하는 메서드
    @JsonCreator
    public static UserRole from(String role) {

        for (UserRole userRole : UserRole.values()) {
            if (userRole.name().equals(role)) {
                return userRole;
            }
        }

        throw new UserException(ErrorCode.USER_ROLE_INVALID);
    }
}
