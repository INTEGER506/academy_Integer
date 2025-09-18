package com.ac.kr.academy.domain.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 접속 기록 관리
*/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogHistory {
    private Long id;
    private Long userId;
    private String username;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;
    private String ipAddress;
}
