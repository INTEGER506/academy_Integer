package com.ac.kr.academy.domain.user;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String email;
    private String password;
    private String phone;
    private String name;
    private String role;    //DEFAULT 'USER' / 'student', 'teacher', 'admin'

    private String username;        //로그인에 사용하는 ID(학번, 교수번호 등)
    private boolean passwordTemp;   //임시 비밀번호 상태인지 확인 (1: 임시비번, 0: 임시비번 아님)
}
