# Academy Management System

## 개발환경
- IDE: IntelliJ IDEA
- Server: Tomcat9
- Java: JDK SE 11
- Build: Gradle
- RDBMS: Oracle 21c XE
- ORM: Mybatis
- Template Engine: JSP
- Spring Boot:  2.7.18

## Admin 로그인
- usernamd(로그인ID) : admin
- password : admin123
  - test 폴더 밑 AdminLoginTest에 암호화된 비밀번호 적혀있으니 참고
    - 비밀번호 불일치 시 해당 클래스 실행해서 암호화된 비밀번호 얻으면 됨
- 로그인 API
  - POST /api/auth/login

## 사용자 로그인
- 어드민 계정으로 로그인 후 사용자 계정 생성으로 로그인 ID와 임시 비밀번호 생성 후 가능
