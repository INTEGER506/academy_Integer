package com.ac.kr.academy.domain.user;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;    // 로그인에 사용하는 ID(학번, 교수번호 등)
    private String email;
    private String password;
    private String phone;
    private String name;
    private String role;    //DEFAULT 'USER' / 'student', 'teacher', 'admin'
}
