package com.ticketing.order.common.auditor;

import com.ticketing.order.config.SecurityUtil;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Service;

@Service
public class UserAuditorAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {

        Long userId = SecurityUtil.getId();
        if (userId != null && userId > 0L) {
            return Optional.of(SecurityUtil.getId());
        } else {
            return Optional.empty();
        }
    }
}
