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
