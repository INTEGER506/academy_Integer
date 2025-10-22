package com.ac.kr.academy.dto.auth;

import lombok.Data;

/**
 * 비밀번호 변경 DTO
 * */

@Data
public class ChangePasswordDTO {
    private String currentPassword; //현재 비밀번호 (변경 전)
    private String newPassword;     //새 비밀번호
}
