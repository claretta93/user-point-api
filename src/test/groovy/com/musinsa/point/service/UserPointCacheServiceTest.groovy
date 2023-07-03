package com.musinsa.point.service

import com.musinsa.point.model.PointStatus
import com.musinsa.point.repository.RedisEvictTargetRepository
import com.musinsa.point.repository.UserPointRepository
import spock.lang.Specification

import java.time.LocalDate

class UserPointCacheServiceTest extends Specification {

    private UserPointCacheService sut

    private final UserPointRepository userPointRepository = Mock()
    private final RedisEvictTargetRepository redisEvictTargetRepository = Mock()

    def setup() {
        sut = new UserPointCacheService(userPointRepository, redisEvictTargetRepository)
    }

    def "사용자 잔여 포인트 조회"() {
        given:
        def userId = 1L
        userPointRepository.findRemainPointByUserIdAndStatusNot(1L, PointStatus.CANCEL, LocalDate.now())
                >> Optional.of(100)
        def expected = 100

        when:
        def actual = sut.getRemainPoint(userId, LocalDate.now())

        then:
        actual == expected
    }

    def "사용자 잔여 포인트 조회 - 조회 결과가 없으면 0을 반환한다."() {
        given:
        def userId = 1L
        userPointRepository.findRemainPointByUserIdAndStatusNot(1L, PointStatus.CANCEL, LocalDate.now())
                >> Optional.empty()
        def expected = 0

        when:
        def actual = sut.getRemainPoint(userId, LocalDate.now())

        then:
        actual == expected
    }

    def "사용자 잔여 포인트 조회 - 조회 결과가 음수이면 0을 반환한다."() {
        given:
        def userId = 1L
        userPointRepository.findRemainPointByUserIdAndStatusNot(1L, PointStatus.CANCEL, LocalDate.now())
                >> Optional.of(-300)
        def expected = 0

        when:
        def actual = sut.getRemainPoint(userId, LocalDate.now())

        then:
        actual == expected
    }
}
