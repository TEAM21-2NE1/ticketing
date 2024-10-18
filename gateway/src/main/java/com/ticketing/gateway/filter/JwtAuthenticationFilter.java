package com.ticketing.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtAuthenticationFilter implements ServerSecurityContextRepository {

    @Value("${service.jwt.secret-key}")
    private String secretKey;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return null;
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {

        String path = exchange.getRequest().getURI().getPath();

        // 로그인, 회원가입 경로는 필터 제외
        if (path.equals("/api/v1/auth/sign-in")
                || path.equals("/api/v1/auth/sign-up")
                || path.equals("/api/v1/payments/view")) {
            return Mono.empty();
        }

        // JWT 토큰 추출
        String token = resolveToken(exchange);
        if (token == null) {
            log.info("No JWT token found for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return Mono.empty();
        }

        // 토큰 검증
        Claims claims = validateToken(token);
        if (claims == null) {
            log.info("JWT token validation failed for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return Mono.empty();
        }

        String id = String.valueOf(claims.get("userId"));
        String email = (String) claims.get("userEmail");
        String role = (String) claims.get("role");

        //username, null, role
        Collection<GrantedAuthority> roleCollection = List.of(() -> role);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                email, null, roleCollection);

        exchange.mutate()
                .request(r -> r.headers(headers -> {
                    headers.add("X-User-Id", id);
                    headers.add("X-User-Email", email);
                    headers.add("X-User-Role", role);
                }))
                .build();

        return Mono.just(new SecurityContextImpl(authentication));  // SecurityContext 생성
    }

    // JWT 토큰을 추출하는 메서드
    private String resolveToken(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    // JWT 토큰을 검증하고 Claims 객체를 반환하는 메서드
    private Claims validateToken(String token) {
        SecretKey key = generateSigningKey(secretKey);
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT token: {}", e.getMessage());
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument during JWT parsing: {}", e.getMessage());
        }
        return null;  // 토큰 검증 실패 시 null 반환
    }

    public SecretKey generateSigningKey(String base64SecretKey) {
        byte[] decodedKey = Base64.getDecoder().decode(base64SecretKey);
        return Keys.hmacShaKeyFor(decodedKey);
    }
}
