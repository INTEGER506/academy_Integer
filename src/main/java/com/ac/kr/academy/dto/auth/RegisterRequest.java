package com.ac.kr.academy.dto.auth;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class RegisterRequest {
    @NotBlank
    private String username;    //학번, 교수번호등 로그인에 사용되는 ID

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    private String phone;

    @NotBlank
    private String name;
}
