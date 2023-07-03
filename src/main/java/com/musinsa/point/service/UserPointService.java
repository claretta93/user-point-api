package com.musinsa.point.service;

import com.musinsa.point.dto.PointHistoryPageResponse;
import com.musinsa.point.dto.RemainPointResponse;
import com.musinsa.point.dto.UserPointRequest;
import com.musinsa.point.entity.UserPointEntity;
import com.musinsa.point.exception.RequestDuplicatedException;
import com.musinsa.point.exception.RemainNotEnoughException;
import com.musinsa.point.exception.RequestNotFoundException;
import com.musinsa.point.model.PointStatus;
import com.musinsa.point.model.RequestLockKey;
import com.musinsa.point.repository.UserPointRepository;
import java.time.LocalDate;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserPointService {

    private final UserPointCacheService userPointCacheService;
    private final LockService lockService;
    private final UserPointRepository userPointRepository;

    public UserPointService(UserPointCacheService userPointCacheService, LockService lockService,
        UserPointRepository userPointRepository) {
        this.userPointCacheService = userPointCacheService;
        this.lockService = lockService;
        this.userPointRepository = userPointRepository;
    }

    @Transactional(readOnly = true)
    public RemainPointResponse getRemainPoint(Long userId) {
        var today = LocalDate.now();
        return new RemainPointResponse(userId, userPointCacheService.getRemainPoint(userId, today));
    }

    @Transactional(readOnly = true)
    public PointHistoryPageResponse getPointHistory(Long userId, Pageable pageable) {
        var pointHistoryPage = userPointRepository.findAllByUserIdAndStatusNot(userId, PointStatus.CANCEL, pageable);
        return new PointHistoryPageResponse(userId, pointHistoryPage);
    }

    @Transactional
    public void addPoint(UserPointRequest request) {
        validateRequest(request.getRequestId(), request.getRequestedBy(), PointStatus.ADD);
        try {
            userPointRepository.save(UserPointEntity.add(request));
            userPointCacheService.evictRemainPointCache(request.getUserId(), LocalDate.now());
        } catch (DuplicateKeyException e) {
            throw new RequestDuplicatedException();
        }
    }

    @Transactional
    public void usePoint(UserPointRequest request) {
        var today = LocalDate.now();
        if (userPointCacheService.getRemainPoint(request.getUserId(), today) < request.getAmount()) {
            throw new RemainNotEnoughException();
        }
        validateRequest(request.getRequestId(), request.getRequestedBy(), PointStatus.USE);
        try {
            userPointRepository.save(UserPointEntity.use(request));
            userPointCacheService.evictRemainPointCache(request.getUserId(), today);
        } catch (DuplicateKeyException e) {
            throw new RequestDuplicatedException();
        }
    }

    @Transactional
    public void cancelPointUse(Long requestId, String requestedBy) {
        var userPointEntity =
            userPointRepository.findByRequestIdAndRequestedByAndStatus(requestId, requestedBy, PointStatus.USE)
                .orElseThrow(() -> new RequestNotFoundException())
                .cancel();
        userPointCacheService.evictRemainPointCache(userPointEntity.getUserId(), LocalDate.now());
    }

    private void validateRequest(Long requestId, String requestedBy, PointStatus status) {
        if (userPointRepository.findByRequestIdAndRequestedByAndStatus(requestId, requestedBy, status).isPresent()
            || !lockService.acquire(RequestLockKey.getKey(requestId, requestedBy, PointStatus.USE))) {
            throw new RequestDuplicatedException();
        }
    }
}
