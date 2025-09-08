package com.ac.kr.academy.domain.user;

import lombok.Data;

/**
 * 긴급공지 전체 사용자 알림 때문에 임시 생성 / 성적
 * */
@Data
public class User {
    private Long id;
    private String email;
    private String password;
    private String phone;
    private String name;
    private String role;    //DEFAULT 'USER' / 'student', 'teacher', 'admin'
}
