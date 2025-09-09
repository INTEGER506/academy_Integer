package com.ac.kr.academy.dto.auth;

import lombok.Data;

/**
 * 비밀번호 변경 DTO
 * */

@Data
public class ChangePassword {
    private String currentPassword;
    private String newPassword;
}
