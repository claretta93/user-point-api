package com.musinsa.point.repository;

import com.musinsa.point.model.entity.UserPointEntity;
import com.musinsa.point.model.PointStatus;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPointRepository extends JpaRepository<UserPointEntity, Long> {

    @Query("select sum(upe.amount) from UserPointEntity upe "
        + "where upe.userId = :userId "
        + "and upe.status <> :status "
        + "and (upe.expireDate is null or upe.expireDate >= :today)")
    Optional<Integer> findRemainPointByUserIdAndStatusNot(@Param("userId") Long userId, @Param("status") PointStatus status, @Param("today") LocalDate today);
    Page<UserPointEntity> findAllByUserIdAndStatusNot(Long userId, PointStatus status, Pageable pageable);
    Optional<UserPointEntity> findByRequestIdAndRequestedByAndStatus(Long requestId, String requestedBy, PointStatus status);
}
