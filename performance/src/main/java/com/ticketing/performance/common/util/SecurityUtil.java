package com.ticketing.performance.common.util;

import com.ticketing.performance.common.exception.ForbiddenAccessException;
import com.ticketing.performance.common.response.ErrorCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Long getId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getDetails();
    }

    public static String getRole() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(()-> new ForbiddenAccessException(ErrorCode.FORBIDDEN_ACCESS));
    }
}
