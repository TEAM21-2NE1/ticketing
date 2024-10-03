package com.ticketing.review.common.filter;

import com.ticketing.review.common.context.UserContext;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GlobalAuthFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain filterChain) throws IOException, ServletException {
    try {
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      log.info("======Global Auth Filter======");
      UserContext.setUserId(Long.parseLong(httpRequest.getHeader("X-User-Id")));
      UserContext.setUserEmail(httpRequest.getHeader("X-User-Email"));
      UserContext.setUserRole(httpRequest.getHeader("X-User-Role"));

      log.info("GlobalAuthFilter - userId : {}", UserContext.getUserId());
      log.info("GlobalAuthFilter - userEmail : {}", UserContext.getUserEmail());
      log.info("GlobalAuthFilter - userRole : {}", UserContext.getUserRole());

      filterChain.doFilter(request, response);
    } finally {
      UserContext.clear();
    }
  }
}
