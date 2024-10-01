package com.ticketing.user.infrastructure.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";

    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";

    @Value("${spring.application.name}")
    private String issuer;

    @Value("${service.jwt.access-expiration}")
    private Long accessExpiration;

    @Value("${service.jwt.secret-key}")
    private String secretKey;

    private Key key;    // secretKey를 Base64로 디코딩 한 값

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // JWT 토큰 생성
    public String createAccessToken(String userEmail, Long userId, String role) {

        Date now = new Date();

        return Jwts.builder()
                // 사용자 ID를 클레임으로 설정
                .claim("userEmail", userEmail) // 사용자 이름 추가
                .claim("userId", userId)       // 사용자 ID 추가
                .claim("role", role)           // 역할(권한) 추가
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(now.getTime() + accessExpiration))
                .signWith(key)
                .compact();
    }
}
