package com.ac.kr.academy.domain.auth;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RefreshToken {
    private Long id;
    private Long userId;                //리프레시 토큰 소유자의 id
    private String token;               //실제 리프레시 토큰 문자열
    private LocalDateTime expiryDate;   //만료일자 - 자동삭제
}
