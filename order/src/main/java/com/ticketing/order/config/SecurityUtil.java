package com.ticketing.order.config;

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
                .orElseThrow(()-> new RuntimeException("FORBIDDEN_ACCESS"));
    }

    public static String getEmail() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
