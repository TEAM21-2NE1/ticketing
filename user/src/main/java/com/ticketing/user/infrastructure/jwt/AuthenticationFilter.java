package com.ticketing.user.infrastructure.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketing.user.application.dto.request.LoginUserRequestDto;
import com.ticketing.user.common.exception.UserException;
import com.ticketing.user.common.response.ErrorCode;
import com.ticketing.user.domain.model.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;

    public AuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        super(authenticationManager);
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/v1/auth/sign-in");  // 로그인 요청 경로 설정
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // 요청 본문에서 이메일과 비밀번호를 추출
            LoginUserRequestDto creds = new ObjectMapper().readValue(request.getInputStream(), LoginUserRequestDto.class);

            // 사용자 인증을 시도
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(creds.email(), creds.password(), new ArrayList<>());

            return getAuthenticationManager().authenticate(authenticationToken);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // 인증이 성공시, JWT 생성

        // authResult에서 사용자 이름, ID, 권한 정보 추출
        Long userId = ((UserDetailsImpl)authResult.getPrincipal()).getUserId();  // 사용자 ID (예: UserDetails에서 추출)
        String userEmail = authResult.getName(); // username (principal)
        String role = authResult.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new UserException(ErrorCode.USER_ROLE_INVALID)); // 권한이 없으면 예외 발생

        String token = jwtUtil.createAccessToken(userEmail, userId, role);

        // 응답 헤더에 JWT를 추가
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, JwtUtil.BEARER_PREFIX + token);

        // 응답 메시지를 JSON으로 작성
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK); // HTTP 상태 코드 200
        response.getWriter().write("{\"message\": \"로그인을 성공했습니다.\"}");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        // 로그로 인증 실패 원인 기록
        log.error("Authentication failed: {}", failed.getMessage());

        // 응답 메세지를 JSON으로 작성
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // HTTP 상태 코드 401
        response.getWriter().write("{\"message\": \"로그인에 실패했습니다.\"}");
    }
}
