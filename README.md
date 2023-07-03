# user-point-api

### 실행방법

* IntelliJ 사용 권장
* Project structure(⌘ + ;) -> sdk: julu-11, language level: 11
* user-point-api 경로에서 docker-compose up 입력하여 redis 구동 완료 후 UserPointApiApplication 실행
* docker-compose down 입력하여 redis 종료

### swagger
* localhost:8080/swagger-ui.html 접속하여 API 명세 확인

### DB 접속
* localhost:8080/h2-console 접속하여 DB 확인

### 주의사항
* Table이 제대로 생성되지 않아 서버 구동 실패 시, UserPointApiApplication 재기동