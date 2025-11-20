# Academy Management System

학원 통합 관리 시스템은 학생, 교수, 관리자가 사용하는 종합적인 학사 관리 플랫폼입니다.

## 📋 목차

- [주요 기능](#주요-기능)
- [기술 스택](#기술-스택)
- [시스템 요구사항](#시스템-요구사항)
- [설치 및 실행](#설치-및-실행)
- [데이터베이스 설정](#데이터베이스-설정)
- [로그인 정보](#로그인-정보)
- [프로젝트 구조](#프로젝트-구조)
- [주요 기능 상세](#주요-기능-상세)
- [API 엔드포인트](#api-엔드포인트)

## 🎯 주요 기능

### 공통 기능
- **학사일정 관리**: 학사 일정 등록 및 조회
- **공지사항**: 공지사항 작성, 수정, 삭제, 조회
- **알림센터**: 시스템 알림 확인 및 관리
- **마이페이지**: 개인 정보 조회 및 수정
- **인증/인가**: JWT 기반 로그인 및 권한 관리

### 관리자 (ROLE_ADMIN)
- **사용자 계정 관리**: 학생, 교수, 직원 계정 생성 및 관리
- **강의 관리**: 전체 강의 목록 조회 및 폐강 처리
- **학기 관리**: 학기 등록 및 현재 학기 설정
- **지도교수 지정**: 학생별 지도교수 배정
- **성적 규정 관리**: 전체 규정 및 과목별 규정 설정
- **접속 기록 관리**: 사용자 접속 이력 조회
- **로그 모니터링**: 실시간 시스템 로그 모니터링

### 교수 (ROLE_PROFESSOR)
- **강의 개설**: 강의 개설 및 관리
- **강의 목록**: 담당 강의 목록 조회
- **시간표 조회**: 본인의 강의 시간표 확인
- **성적 관리**: 학생 성적 입력, 수정, 삭제
- **점수 분배 설정**: 중간고사, 기말고사, 과제 등 점수 비율 설정

### 학생 (ROLE_STUDENT)
- **수강 신청**: 강의 수강 신청 및 취소
- **수강 목록**: 본인의 수강 목록 조회
- **시간표 조회**: 본인의 수강 시간표 확인
- **성적 조회**: 학기별 성적 조회 및 상세 확인

## 🛠 기술 스택

### Backend
- **Framework**: Spring Boot 2.7.18
- **Language**: Java 11 (JDK SE 11)
- **Build Tool**: Gradle
- **Security**: Spring Security + JWT (jjwt 0.11.5)
- **ORM**: MyBatis 2.2.2
- **Database**: Oracle 21c XE
- **Server**: Tomcat 9 (Embedded)

### Frontend
- **Template Engine**: JSP
- **CSS Framework**: Bootstrap
- **JavaScript Library**: jQuery 3.7.1
- **Calendar**: FullCalendar

### Development Tools
- **IDE**: IntelliJ IDEA
- **Validation**: Spring Boot Starter Validation

## 💻 시스템 요구사항

- **JDK**: 11 이상
- **Gradle**: 7.x 이상 (또는 Gradle Wrapper 사용)
- **Database**: Oracle 21c XE
- **Server**: Tomcat 9 (또는 Embedded Tomcat)
- **OS**: Windows, Linux, macOS

## 🚀 설치 및 실행

### 1. 데이터베이스 설정
Oracle 21c XE를 설치하고 데이터베이스를 생성합니다.

### 2. 설정 파일 구성
`src/main/resources/application.properties` 파일에 데이터베이스 연결 정보를 설정합니다.

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
```

### 3. 데이터베이스 초기화
`src/main/resources/dummy.sql` 파일을 실행하여 초기 데이터를 삽입합니다.

### 4. 프로젝트 빌드
```bash
# Windows
gradlew.bat build
```

### 5. 애플리케이션 실행
```bash
# Windows
gradlew.bat bootRun
```

또는 IntelliJ IDEA에서 `AcademyApplication.java`를 실행합니다.

### 6. 접속
브라우저에서 `http://localhost:8080`으로 접속합니다.

## 📊 데이터베이스 설정

### 초기 데이터 삽입
`src/main/resources/dummy.sql` 파일을 실행하여 다음 데이터를 삽입합니다:

- 관리자 계정
- 학과 데이터
- ID 시퀀스 초기화

## 🔐 로그인 정보

### 관리자 계정
- **로그인 ID**: `admin`
- **비밀번호**: `plz123456`
- **권한**: ROLE_ADMIN

> ⚠️ **주의**: 초기 비밀번호는 `src/main/resources/dummy.sql` 파일에서 확인할 수 있습니다.  
> 프로덕션 환경에서는 반드시 비밀번호를 변경하세요.

### 사용자 계정 생성
1. 관리자 계정으로 로그인
2. 계정 관리 메뉴에서 사용자 계정 생성
3. 생성된 로그인 ID와 임시 비밀번호로 로그인
4. 최초 로그인 시 비밀번호 변경 권장


## 🔍 주요 기능 상세

### 인증 및 권한 관리
- JWT 기반 토큰 인증
- Spring Security를 통한 역할 기반 접근 제어
- 비밀번호 암호화 (BCrypt)
- 비밀번호 찾기 및 초기화 기능

### 강의 관리
- 강의 개설, 수정, 삭제
- 강의 시간표 관리 (요일, 시간)
- 정원 관리 및 수강 인원 확인
- 강의 폐강 처리

### 수강 신청
- 수강 신청 가능 강의 목록 조회
- 수강 신청 및 취소
- 학점 제한 관리 (최대 18학점)
- 시간표 충돌 검사

### 성적 관리
- 교수: 학생 성적 입력, 수정, 삭제
- 학생: 학기별 성적 조회
- 성적 규정 관리 (전체 규정, 과목별 규정)
- 점수 분배 비율 설정 (중간고사, 기말고사, 과제 등)
- 학점 평균 계산 및 졸업 요건 확인

### 학기 관리
- 학기 등록 및 관리
- 현재 학기 설정
- 학기별 강의 및 성적 관리

### 로그 관리
- 사용자 접속 기록 저장 및 조회
- 실시간 로그 모니터링
- 시스템 활동 추적

