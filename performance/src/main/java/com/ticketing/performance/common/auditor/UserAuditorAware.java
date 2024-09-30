package com.ticketing.performance.common.auditor;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Service;

@Service
public class UserAuditorAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {

        // TODO: 유저 정보 수정하기
        Long modifiedById = 1L;

        return Optional.of(modifiedById);
    }
}
