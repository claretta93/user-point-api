# user-point-api

### 실행방법

* IntelliJ 사용 권장
* Project structure(⌘ + ;) -> sdk: julu-11, language level: 11
* user-point-api 경로에서 docker-compose up 입력하여 redis 구동 완료 후 UserPointApiApplication 실행

### swagger
* localhost:8080/swagger-ui.html 접속하여 API 명세 확인

### DB 접속
* localhost:8080/h2-console 접속하여 DB 확인

### 주의사항
* docker-compose down 입력하여 redis 종료
* Table이 제대로 생성되지 않아 서버 구동 실패 시, UserPointApiApplication 재기동

### 기능 상세
* 회원의 잔여 포인트를 조회하는 API
  * 잔여 포인트 조회 쿼리는 JPA Query Method 만으로 구현이 어려워 JPQL 사용 
  * 잔여 포인트 조회 쿼리의 비용이 크기 때문에 Redis 캐시 활용
    * Redis에 저장된 데이터의 유효기간은 1일로 설정
    * Redis에 저장된 데이터의 key는 "회원id:일자"로 설정
    * Redis 장애 시에는 별도 조치 없이 @Retryable @Recover 활용하여 DB 조회
***
* 회원의 포인트 적립/사용 내역을 조회하는 API
  * JPA Qyery Method 활용하여 조회
  * page, size 파라미터의 범위는 @Validated 활용하여 제한
***
* 회원의 포인트를 적립하는 API
  * 회원 정보 인증은 별도로 구현하지 않음 
    * 회원 API 호출 혹은 kafka 등으로 전달받아 DB에 적재 후 처리
  * DB 에서 unique key (requestId, requestedBy) 활용하여 중복 적립 방지
    * requestedBy의 경우 유관 부서 협의 후 Enum 등으로 제한해야 어뷰징을 방지할 수 있음
  * DB unique key insert 시 지연이 발생할 수 있기 때문에 Redis를 활용한 lock으로 사전 방지
    * Redis lock의 유효기간은 10초로 설정
    * Redis 장애 시에는 별도 조치 없이 DB unique key 의존
  * 포인트 적립이 완료되면 Redis에 저장된 잔여 포인트 캐시 삭제
    * Redis 장애 시에는 별도 테이블에 이력을 저장, 장애 복구 후 삭제 시도 이력을 조회하여 재시도
***
* 회원의 포인트를 사용하는 API
  * 사용액의 경우 요청은 양수로 받으나, DB에는 음수로 변환하여 저장
***
* 회원의 포인트 사용을 취소하는 API
  * 요청 이력은 requestId, requestedBy로 확인
  * 이력을 삭제하지 않고 상태값만 변경하여 API를 통해서는 조회할 수 없지만 DB에는 이력이 남아있도록 함