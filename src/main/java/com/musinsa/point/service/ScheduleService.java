package com.musinsa.point.service;

import com.musinsa.point.model.RedisEvictTargetStatus;
import com.musinsa.point.repository.RedisEvictTargetRepository;
import java.time.LocalDate;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScheduleService {

    private static final int PAGE_SIZE = 100;

    private final UserPointCacheService userPointCacheService;
    private final RedisEvictTargetRepository redisEvictTargetRepository;

    public ScheduleService(UserPointCacheService userPointCacheService,
        RedisEvictTargetRepository redisEvictTargetRepository) {
        this.userPointCacheService = userPointCacheService;
        this.redisEvictTargetRepository = redisEvictTargetRepository;
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 3000)
    @Transactional
    public void evictCorruptedCache() {
        var page = 0;
        while (true) {
            var today = LocalDate.now();
            var targetPage = redisEvictTargetRepository.findAllByStatusAndCreatedAtBetween(
                RedisEvictTargetStatus.TARGET,
                today.atStartOfDay(),
                today.atTime(23, 59, 59),
                PageRequest.of(page, PAGE_SIZE)
            );
            try {
                targetPage.forEach(redisEvictTargetEntity -> {
                    userPointCacheService.healthCheck();
                    userPointCacheService.evictRemainPointCache(redisEvictTargetEntity.getUserId(), today);
                    redisEvictTargetEntity.evicted();
                });
            } catch (Exception e) {
                break;
            }
            if (targetPage.isLast()) {
                break;
            }
            page++;
        }
    }
}
