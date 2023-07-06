package com.musinsa.point.service;

import static java.lang.Math.max;

import com.musinsa.point.model.entity.RedisEvictTargetEntity;
import com.musinsa.point.model.PointStatus;
import com.musinsa.point.model.RedisEvictTargetStatus;
import com.musinsa.point.repository.RedisEvictTargetRepository;
import com.musinsa.point.repository.UserPointRepository;
import java.time.LocalDate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserPointCacheService {

    private final UserPointRepository userPointRepository;
    private final RedisEvictTargetRepository redisEvictTargetRepository;

    public UserPointCacheService(UserPointRepository userPointRepository, RedisEvictTargetRepository redisEvictTargetRepository) {
        this.userPointRepository = userPointRepository;
        this.redisEvictTargetRepository = redisEvictTargetRepository;
    }

    @Cacheable(value = "cacheCheck", key = "1")
    public int healthCheck() {
        return 1;
    }

    @Retryable(maxAttempts = 1)
    @Cacheable(value = "remainPoint", key = "#userId.toString().concat(':').concat(#today.toString())")
    @Transactional(readOnly = true)
    public int getRemainPoint(Long userId, LocalDate today) {
        return max(userPointRepository.findRemainPointByUserIdAndStatusNot(userId, PointStatus.CANCEL, today)
            .orElse(0), 0);
    }

    @Recover
    @Transactional(readOnly = true)
    public int getRemainPoint(Exception e, Long userId, LocalDate today) {
        return max(userPointRepository.findRemainPointByUserIdAndStatusNot(userId, PointStatus.CANCEL, today)
            .orElse(0), 0);
    }

    @Retryable(maxAttempts = 1)
    @CacheEvict(value = "remainPoint", key = "#userId.toString().concat(':').concat(#today.toString())")
    public void evictRemainPointCache(Long userId, LocalDate today) {
    }

    @Recover
    public void evictRemainPointCache(Exception e, Long userId) {
        // TODO : Redis 장애 인지 및 빠른 후속 처리를 위한 알람 전송 로직 추가 필요
        redisEvictTargetRepository.findByUserIdAndStatusAndCreatedAtBetween(userId, RedisEvictTargetStatus.TARGET,
                LocalDate.now().atStartOfDay(), LocalDate.now().atTime(23, 59, 59)
        ).orElseGet(() -> redisEvictTargetRepository.save(new RedisEvictTargetEntity(userId)));
    }
}
