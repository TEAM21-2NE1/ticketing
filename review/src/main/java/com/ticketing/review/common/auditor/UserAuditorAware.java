package com.ticketing.review.common.auditor;

import com.ticketing.review.infrastructure.utils.SecurityUtils;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Service;

@Service
public class UserAuditorAware implements AuditorAware<Long> {

  @Override
  public Optional<Long> getCurrentAuditor() {

    Long userId = SecurityUtils.getUserId();
    if (userId != null && userId > 0L) {
      return Optional.of(SecurityUtils.getUserId());
    } else {
      return Optional.empty();
    }
  }
}