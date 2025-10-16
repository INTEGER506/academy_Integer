--관리자 계정(테스트용)
INSERT INTO users(id, username, password, password_temp, name, email, role, phone)
VALUES (
           user_seq.NEXTVAL,
           'admin',                --로그인ID
           '$2a$10$TjEx.NZBVvxzKwyZ20UBfOlUgPIWNLsl.ja5xVqkuqtTSy3TrTA5G', --암호화된 비밀번호
           0,
           '관리자',                --이름
           'admin@ac.kr',          --이메일
           'ROLE_ADMIN',           --권한
           '010-0000-0000'         --전화번호
       );

--학과 데이터 삽입 (테스트용)
INSERT INTO dept (id, dept_name) VALUES (1, '컴퓨터공학과');


-- 로그인 ID 자동생성 / id_sequences 테이블 데이터 삽입
INSERT INTO id_sequence (sequence_key, sequence_num) VALUES ('PROFESSOR', 0);
INSERT INTO id_sequence (sequence_key, sequence_num) VALUES ('STAFF', 0);

-- 기본 학점 분배 비율 (글로벌 규정) - 퍼센트 기반
-- A+ 10%, A 20%, B+ 20%, B 20%, C+ 15%, C 10%, D+ 3%, D 2%, F 0%
INSERT INTO alphabet_system (id, course_id, alphabet, boundary, description)
VALUES (alphabet_system_seq.NEXTVAL, NULL, 'A+', 10.0, '상위 10%');
INSERT INTO alphabet_system (id, course_id, alphabet, boundary, description)
VALUES (alphabet_system_seq.NEXTVAL, NULL, 'A',  20.0, '상위 30%');
INSERT INTO alphabet_system (id, course_id, alphabet, boundary, description)
VALUES (alphabet_system_seq.NEXTVAL, NULL, 'B+', 20.0, '상위 50%');
INSERT INTO alphabet_system (id, course_id, alphabet, boundary, description)
VALUES (alphabet_system_seq.NEXTVAL, NULL, 'B',  20.0, '상위 70%');
INSERT INTO alphabet_system (id, course_id, alphabet, boundary, description)
VALUES (alphabet_system_seq.NEXTVAL, NULL, 'C+', 15.0, '상위 85%');
INSERT INTO alphabet_system (id, course_id, alphabet, boundary, description)
VALUES (alphabet_system_seq.NEXTVAL, NULL, 'C',  10.0, '상위 95%');
INSERT INTO alphabet_system (id, course_id, alphabet, boundary, description)
VALUES (alphabet_system_seq.NEXTVAL, NULL, 'D+', 3.0, '상위 98%');
INSERT INTO alphabet_system (id, course_id, alphabet, boundary, description)
VALUES (alphabet_system_seq.NEXTVAL, NULL, 'D',  2.0, '상위 100%');

-- 기본 테스트 데이터 추가
-- 교수 데이터
INSERT INTO users(id, username, password, password_temp, name, email, role, phone)
VALUES (user_seq.NEXTVAL, 'prof1', '$2a$10$TjEx.NZBVvxzKwyZ20UBfOlUgPIWNLsl.ja5xVqkuqtTSy3TrTA5G', 0, '김교수', 'prof1@ac.kr', 'ROLE_PROFESSOR', '010-1111-1111');

-- 학생 데이터
INSERT INTO users(id, username, password, password_temp, name, email, role, phone)
VALUES (user_seq.NEXTVAL, 'student1', '$2a$10$TjEx.NZBVvxzKwyZ20UBfOlUgPIWNLsl.ja5xVqkuqtTSy3TrTA5G', 0, '김학생', 'student1@ac.kr', 'ROLE_STUDENT', '010-2222-2222');

-- Professor 테이블 데이터 (users와 연결)
INSERT INTO professor(id, professor_num, created_at, ended_at, dept_id, user_id)
VALUES (professor_seq.NEXTVAL, 'PROF001', SYSDATE, NULL, 1, 2);

-- Student 테이블 데이터 (users와 연결)
INSERT INTO student(id, student_num, status, created_at, ended_at, dept_id, user_id)
VALUES (student_seq.NEXTVAL, '20240001', 'ACTIVE', SYSDATE, NULL, 1, 3);

-- 과목 데이터
INSERT INTO subject(id, name, credit, description, professor_id, dept_id)
VALUES (subject_seq.NEXTVAL, '알고리즘', 3, '알고리즘 기초', 2, 1);

-- 학기 데이터
INSERT INTO semester(id, year, name, enrollmentStartDate, enrollmentEndDate)
VALUES (semester_seq.NEXTVAL, 2024, '1학기', TO_DATE('2024-03-01', 'YYYY-MM-DD'), TO_DATE('2024-06-30', 'YYYY-MM-DD'));

-- 강의 데이터
INSERT INTO course(id, professor_id, subject_id, semester_id, capacity, num_of_student, day_of_week, place, status, time)
VALUES (course_seq.NEXTVAL, 2, 1, 1, 30, 0, '월수', 'A101', 'ACTIVE', '09:00-10:30');

-- 수강신청 데이터
INSERT INTO enrollment(id, course_id, student_id)
VALUES (enrollment_seq.NEXTVAL, 1, 3);
