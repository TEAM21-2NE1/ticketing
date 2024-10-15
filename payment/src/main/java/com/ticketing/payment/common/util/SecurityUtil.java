package com.ticketing.payment.common.util;

import com.ticketing.payment.common.exception.ForbiddenAccessException;
import com.ticketing.payment.common.response.ErrorCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Long getId() {
        return Long.parseLong((String) SecurityContextHolder.getContext().getAuthentication().getDetails());
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
