package com.ticketing.user.common.auditor;

import com.ticketing.user.domain.model.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Service
public class UserAuditorAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {

        // 현재 HTTP 요청을 가져옴
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String path = request.getRequestURI();

        // 회원가입 경로일 경우
        if (path.equals("/api/v1/auth/sign-up")) {
            return Optional.empty(); // 회원가입은 인증정보가 없음
        }

        // Spring Security에서 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보가 없거나, 사용자가 익명 사용자인 경우 처리
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            log.info("인증정보가 존재하지 않습니다.");
            return Optional.empty();
        }

        // 인증된 사용자 정보에서 사용자 ID 가져오기 (UserDetails 또는 Principal 객체에서 추출)

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) principal;
            return Optional.of(userDetails.getUserId());
        }

        return Optional.empty();
    }
}
