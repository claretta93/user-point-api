package com.musinsa.point.model.entity;

import com.musinsa.point.model.dto.UserPointRequest;
import com.musinsa.point.model.PointStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_point", uniqueConstraints = @UniqueConstraint(name = "uk_requestId_requestedBy",
        columnNames = {"request_id", "requested_by"}))
public class UserPointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "request_id", nullable = false, updatable = false)
    private Long requestId;

    // TODO : 어뷰징 방지를 위해 요구사항 정의 후 ENUM 으로 변경하여 제한 필요
    @Column(name = "requested_by", nullable = false, updatable = false)
    private String requestedBy;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "amount")
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PointStatus status;

    @Column(name = "expire_date")
    private LocalDate expireDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    protected LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    protected LocalDateTime updatedAt;

    UserPointEntity(Long requestId, String requestedBy, Long userId, Integer amount, PointStatus status, LocalDate expireDate) {
        this.requestId = requestId;
        this.requestedBy = requestedBy;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.expireDate = expireDate;
    }

    public static UserPointEntity add(UserPointRequest request) {
        return new UserPointEntity(
            request.getRequestId(),
            request.getRequestedBy(),
            request.getUserId(),
            request.getAmount(),
            PointStatus.ADD,
            LocalDate.now().plusYears(1)
        );
    }

    public static UserPointEntity use(UserPointRequest request) {
        return new UserPointEntity(
            request.getRequestId(),
            request.getRequestedBy(),
            request.getUserId(),
            -request.getAmount(),
            PointStatus.USE,
            null
        );
    }

    public UserPointEntity cancel() {
        this.status = PointStatus.CANCEL;
        return this;
    }

    public boolean isExpired() {
        return (this.expireDate != null && this.expireDate.isBefore(LocalDate.now()));
    }
}
