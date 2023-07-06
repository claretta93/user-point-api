package com.musinsa.point.repository;

import com.musinsa.point.model.entity.RedisEvictTargetEntity;
import com.musinsa.point.model.RedisEvictTargetStatus;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisEvictTargetRepository extends JpaRepository<RedisEvictTargetEntity, Long> {

    Page<RedisEvictTargetEntity> findAllByStatusAndCreatedAtBetween(RedisEvictTargetStatus status, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Optional<RedisEvictTargetEntity> findByUserIdAndStatusAndCreatedAtBetween(Long userId, RedisEvictTargetStatus status, LocalDateTime start, LocalDateTime end);
}
