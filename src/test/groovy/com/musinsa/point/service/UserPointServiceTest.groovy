package com.musinsa.point.service

import com.musinsa.point.model.dto.UserPointRequest
import com.musinsa.point.model.entity.UserPointEntity
import com.musinsa.point.exception.RequestDuplicatedException
import com.musinsa.point.exception.RemainNotEnoughException
import com.musinsa.point.exception.RequestNotFoundException
import com.musinsa.point.model.PointStatus
import com.musinsa.point.repository.UserPointRepository
import spock.lang.Specification

import java.time.LocalDate

class UserPointServiceTest extends Specification {

    private UserPointService sut

    private final UserPointCacheService userPointCacheService = Mock()
    private final LockService lockService = Mock()
    private final UserPointRepository userPointRepository = Mock()

    def setup() {
        sut = new UserPointService(userPointCacheService, lockService, userPointRepository)
    }

    def "사용자 포인트 적립 - 포인트 적립이 성공하면 사용자 잔여 포인트 캐시를 삭제한다."() {
        given:
        def request = new UserPointRequest(1L, 111111L, "write_review", 100)
        userPointRepository.findByRequestIdAndRequestedByAndStatus(111111L, "write_review", PointStatus.ADD)
                >> Optional.empty()
        lockService.acquire(_) >> true

        when:
        sut.addPoint(request)

        then:
        1 * userPointRepository.save({
            it.requestId == 111111L
            it.requestedBy == "write_review"
            it.userId == 1L
            it.amount == 100
            it.status == PointStatus.ADD;
            it.expireDate == LocalDate.now().plusYears(1)
        } as UserPointEntity)

        1 * userPointCacheService.evictRemainPointCache(1L, LocalDate.now())
    }

    def "사용자 포인트 적립 - 이미 적립된 요청이 들어올 경우 RequestDuplicatedException"() {
        given:
        def request = new UserPointRequest(1L, 111111L, "write_review", 100)
        userPointRepository.findByRequestIdAndRequestedByAndStatus(111111L, "write_review", PointStatus.ADD)
                >> Optional.of(Stub(UserPointEntity))
        lockService.acquire(_) >> true

        when:
        sut.addPoint(request)

        then:
        thrown(RequestDuplicatedException.class)

        and:
        0 * userPointRepository.save(_)
    }

    def "사용자 포인트 적립 - 중복 요청이 들어올 경우 RequestDuplicatedException"() {
        given:
        def request = new UserPointRequest(1L, 111111L, "write_review", 100)
        userPointRepository.findByRequestIdAndRequestedByAndStatus(111111L, "write_review", PointStatus.ADD)
                >> Optional.empty()
        lockService.acquire(_) >> false

        when:
        sut.addPoint(request)

        then:
        thrown(RequestDuplicatedException.class)

        and:
        0 * userPointRepository.save(_)
    }

    def "사용자 포인트 사용 - 포인트 사용이 성공하면 사용자 잔여 포인트 캐시를 삭제한다. "() {
        given:
        def request = new UserPointRequest(1L, 111111L, "order", 500)
        userPointCacheService.getRemainPoint(1L, _) >> 1000
        userPointRepository.findByRequestIdAndRequestedByAndStatus(111111L, "order", PointStatus.USE)
                >> Optional.empty()
        lockService.acquire(_) >> true

        when:
        sut.usePoint(request)

        then:
        1 * userPointRepository.save({
            it.requestId == 111111L
            it.requestedBy == "order"
            it.userId == 1L
            it.amount == -500
            it.status == PointStatus.USE;
            it.expireDate == null
        } as UserPointEntity)

        1 * userPointCacheService.evictRemainPointCache(1L, LocalDate.now())
    }

    def "사용자 포인트 사용 - 남은 포인트보다 큰 금액 사용 요청이 올 경우 RemainNotEnoughException"() {
        given:
        def request = new UserPointRequest(1L, 111111L, "order", 500)
        userPointCacheService.getRemainPoint(1L, _) >> 300
        userPointRepository.findByRequestIdAndRequestedByAndStatus(111111L, "order", PointStatus.USE)
                >> Optional.empty()
        lockService.acquire(_) >> true

        when:
        sut.usePoint(request)

        then:
        thrown(RemainNotEnoughException.class)

        and:
        0 * userPointRepository.save(_)
    }

    def "사용자 포인트 사용 취소"() {
        given:
        def userPointEntity = Spy(UserPointEntity) {
            getRequestId() >> 111111L
            getRequestedBy() >> "order"
            getStatus() >> PointStatus.USE
        }
        userPointRepository.findByRequestIdAndRequestedByAndStatus(111111L, "order", PointStatus.USE)
                >> Optional.of(userPointEntity)

        when:
        sut.cancelPointUse(111111L, "order")

        then:
        1 * userPointEntity.cancel()
    }

    def "사용자 포인트 사용 취소 - RequestNotFoundException"() {
        given:
        userPointRepository.findByRequestIdAndRequestedByAndStatus(111111L, "order", PointStatus.USE)
                >> Optional.empty()

        when:
        sut.cancelPointUse(111111L, "order")

        then:
        thrown(RequestNotFoundException.class)
    }
}
