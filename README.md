## Spring boot amqp 
* rabbitmp 사용하는 테스트 중심 메세지 처리 방법
* 컨트롤러 : Http요청 받고 -> 메세지 변환 -> 브로커 전달
* 메세지 브로커 : 새 메세지 받을 준비 -> 메세지 확인 -> 디비 저장

### 개발환경
- spring boot 2.7.16
- java 1.8
- webFlux
- embed Mongodb
- amqp
- rabbitmq testconteiner
- jupiter testconteiner
- docker

### 실행
----
테스트 케이스 실행
