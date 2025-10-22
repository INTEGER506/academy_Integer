package com.ac.kr.academy.domain.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    //jsp 에서는 LocalDateTime 사용 불가로 String 으로 변환(JSTL 버전 문제)
    public String getLoginTimeStr(){
        return loginTime != null ? loginTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
    }

    public String getLogoutTimeStr(){
        return logoutTime != null ? logoutTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
    }
}
