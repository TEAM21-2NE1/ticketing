package com.ticketing.performance.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    // 헤더에서 사용자 정보와 역할(Role)을 추출
    String userEmail = request.getHeader("X-User-Email");
    String userRole = request.getHeader("X-User-Role");
    Long userId = Long.parseLong(request.getHeader("X-User-Id"));


    if (userEmail != null && userRole != null) {
      // rolesHeader에 저장된 역할을 SimpleGrantedAuthority로 변환
      List<SimpleGrantedAuthority> authorities = Arrays.stream(userRole.split(","))
          .map(role -> new SimpleGrantedAuthority(role.trim()))
          .collect(Collectors.toList());

      // 사용자 정보를 기반으로 인증 토큰 생성
      UsernamePasswordAuthenticationToken authenticationToken =
          new UsernamePasswordAuthenticationToken(userEmail, null, authorities);

      // userId를 추가 정보로 설정
      authenticationToken.setDetails(userId);

      // SecurityContext에 인증 정보 설정
      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    } else {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 헤더가 없거나 역할 정보가 없으면 401 응답
      return;
    }

    // 필터 체인을 계속해서 진행
    filterChain.doFilter(request, response);
  }
}
