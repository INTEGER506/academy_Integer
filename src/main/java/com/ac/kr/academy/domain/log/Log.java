package com.ac.kr.academy.domain.log;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 접속기록 관리
*/

@Data
public class Log {
    private Long id;
    private String username;
    private String action;      //Login, Logout, 정보수정, 계정생성 등
    private LocalDateTime actionTime;
    private LocalDateTime createAt;
}
