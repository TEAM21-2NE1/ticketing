package com.ticketing.performance.application.scheduler;

import com.ticketing.performance.application.service.RankService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class RankScheduler {

    private final RankService rankService;
    private final CacheManager rankCacheManager;

    @Scheduled(cron = "0 0 * * * *")
    public void run() {

        clearCache();
        rankService.getRank();
    }

    private void clearCache() {
        Objects.requireNonNull(rankCacheManager.getCache("getRank")).clear();
    }


}
