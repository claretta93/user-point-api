package com.musinsa.point.repository

import com.musinsa.point.model.dto.UserPointRequest
import com.musinsa.point.model.entity.UserPointEntity
import com.musinsa.point.model.PointStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import spock.lang.Specification

import java.time.LocalDate

@DataJpaTest
class UserPointRepositoryTest extends Specification{

    @Autowired
    private UserPointRepository sut

    def "사용자 잔여 포인트를 조회한다."() {
        given:
        def addRequest1 = new UserPointRequest(1L, 111111L, "write_review", 300)
        def addRequest2 = new UserPointRequest(1L, 222222L, "write_review", 700)
        def useRequest = new UserPointRequest(1L, 111111L, "order", 500)
        def userPointEntityList = List.of(
                UserPointEntity.add(addRequest1), UserPointEntity.add(addRequest2),
                UserPointEntity.use(useRequest)
        )
        def expected = 500

        when:
        sut.saveAll(userPointEntityList)
        def readEntity = sut.findRemainPointByUserIdAndStatusNot(1L, PointStatus.CANCEL, LocalDate.now())

        then:
        readEntity.isPresent()
        readEntity.get() == expected
    }

    def "사용자 포인트 이력을 조회한다."() {
        given:
        def addRequest1 = new UserPointRequest(1L, 111111L, "write_review", 300)
        def addRequest2 = new UserPointRequest(1L, 222222L, "write_review", 700)
        def useRequest = new UserPointRequest(1L, 111111L, "order", 500)
        def userPointEntityList = List.of(
                UserPointEntity.add(addRequest1), UserPointEntity.add(addRequest2),
                UserPointEntity.use(useRequest)
        )

        when:
        sut.saveAll(userPointEntityList)
        def readEntity = sut.findAllByUserIdAndStatusNot(1L, PointStatus.CANCEL, PageRequest.of(0, 10))

        then:
        readEntity.totalElements == 3L
    }

    def "포인트 사용 요청 이력을 조회한다."() {
        given:
        def useRequest = new UserPointRequest(1L, 111111L, "order", 500)
        def userPointEntity = UserPointEntity.use(useRequest)

        when:
        sut.save(userPointEntity)
        def readEntity = sut.findByRequestIdAndRequestedByAndStatus(111111L, "order", PointStatus.USE)

        then:
        readEntity.isPresent()
        readEntity.get().getUserId() == 1L
        readEntity.get().getAmount() == -500
    }
}
