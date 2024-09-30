package com.ticketing.user.common.auditor;

import com.ticketing.user.domain.model.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Service
public class UserAuditorAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {

        // TODO: 유저 정보 조회 수정하기
//        Long modifiedById = 1L;

        // Spring Security에서 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        // 인증된 사용자 정보에서 사용자 ID 가져오기 (UserDetails 또는 Principal 객체에서 추출)
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long modifiedById = userDetails.getUserId(); // 사용자 ID를 가져옴

        return Optional.of(modifiedById);
    }
}
