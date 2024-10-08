package com.ticketing.user.infrastructure.configuration;

import com.ticketing.user.application.service.UserService;
import com.ticketing.user.infrastructure.jwt.AuthenticationFilter;
import com.ticketing.user.infrastructure.jwt.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // AuthenticationManager를 별도의 Bean으로 등록
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, UserService userService) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)  // CSRF 보호 비활성화
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/auth/sign-up").permitAll()  // 회원가입 요청 허용
                        .requestMatchers("/api/v1/auth/sign-in").permitAll()  // 로그인 요청 허용
                        .requestMatchers("/api/v1/users/**").permitAll()
                        .requestMatchers("/user-service/**").permitAll()
                        .anyRequest().authenticated()  // 그 외 모든 요청은 인증 필요
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 세션 사용하지 않음
                .addFilter(new AuthenticationFilter(authenticationManager, jwtUtil));  // 커스텀 필터 추가

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

