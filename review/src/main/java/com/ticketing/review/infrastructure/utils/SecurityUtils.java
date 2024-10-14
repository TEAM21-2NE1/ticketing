package com.ticketing.review.infrastructure.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtils {

  public static Long getUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    Long userId = null;
    if (authentication != null) {
      userId = Long.parseLong(authentication.getDetails().toString());
      log.info("userId = {}", userId);
    }

    return userId;
  }


  public static String getUserRole() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    String userRole = null;
    if (authentication != null) {
      userRole = authentication.getAuthorities().stream().findFirst().get().getAuthority();
      log.info("userRole = {}", userRole);
    }

    return userRole;
  }

  public static String getUserEmail() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    String userEmail = null;
    if (authentication != null) {
      userEmail = authentication.getPrincipal().toString();
    }

    return userEmail;
  }
}
