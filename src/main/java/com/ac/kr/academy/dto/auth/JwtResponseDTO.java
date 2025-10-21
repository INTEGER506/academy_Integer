package com.ac.kr.academy.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class JwtResponseDTO {

    private String accessToken;
    private String refreshToken;

    @Builder.Default
    private String tokenType = "Bearer ";

    private boolean tempPassword; //임시 비밀번호 상태임을 클라이언트에 전달

}
