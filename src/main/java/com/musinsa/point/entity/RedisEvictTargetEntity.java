package com.musinsa.point.entity;

import com.musinsa.point.model.RedisEvictTargetStatus;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "redis_evict_target")
public class RedisEvictTargetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private RedisEvictTargetStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    protected LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    protected LocalDateTime updatedAt;

    public RedisEvictTargetEntity(Long userId) {
        this.userId = userId;
        this.status = RedisEvictTargetStatus.TARGET;
    }

    public RedisEvictTargetEntity evicted() {
        this.status = RedisEvictTargetStatus.EVICTED;
        return this;
    }
}
