package com.ac.kr.academy.dto.auth;

import lombok.Data;

/**
 * 비밀번호 찾기 DTO
 * */

@Data
public class FindPasswordDTO {
    //사용자 검증을 위한 정보
    private String username;
    private String name;
    private String email;

    //재설정할 새 비밀번호
    private String newPassword;
}