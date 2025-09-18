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
- username(로그인ID) : admin
- password : admin123
 - DB users 관리자용 추가
   - INSERT INTO users(id, username, password, password_temp, name, email, role, phone)
   -  VALUES (
   -  user_seq.NEXTVAL,
   -  'admin',                --로그인ID
   -  '$2a$10$TjEx.NZBVvxzKwyZ20UBfOlUgPIWNLsl.ja5xVqkuqtTSy3TrTA5G', --암호화된 비밀번호
   -  0,
   -  '관리자',                --이름
   -  'admin@ac.kr',          --이메일
   -  'ROLE_ADMIN',           --권한
   -  '010-0000-0000'         --전화번호
   -  );

- 로그인 API
  - POST /api/auth/login

## 사용자 로그인
- 어드민 계정으로 로그인 후 사용자 계정 생성으로 로그인 ID와 임시 비밀번호 생성 후 가능

## POSTMAN 테스트
- 관리자 로그인 / POST / raw
  - {
    "username" : "admin",
    "password" : "admin123"
    }
  - 결과창에서 access-Token 복사
  - Headers에서 Authorization, Content-Type 입력 후 체크박스 체크, 이후 사용자 생성 등 관리자 기능 가능(상세 내용은 이메일 또는 메모장 확인)

-  학생 로그인 ID 자동생성 / POST / x-www-form-urlencoded
  - 예시)
   - {
    "role" : "ROLE_STUDENT",
    "deptId" : 1,
    "email": "student1@test.com",
    "name": "홍길동"
    }
   - 결과 값으로 나오는 로그인 ID와 임시 비밀번호 복사 후 사용자 로그인시 사용
