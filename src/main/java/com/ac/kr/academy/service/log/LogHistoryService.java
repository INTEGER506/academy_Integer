package com.ac.kr.academy.service.log;

import com.ac.kr.academy.domain.log.LogHistory;
import com.ac.kr.academy.mapper.log.LogHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogHistoryService {

    private final LogHistoryMapper logHistoryMapper;

    //로그인 기록 저장
    public void saveLoginLog(Long userId, String username, String ipAddress) {
        LogHistory logHistory = new LogHistory();
        logHistory.setUserId(userId);
        logHistory.setUsername(username);
        logHistory.setLoginTime(LocalDateTime.now());
        logHistory.setIpAddress(ipAddress);

        logHistoryMapper.insertLoginLog(logHistory);
    }

    //로그아웃 시간 업데이트
    public void userLogoutTime(Long userId){
        logHistoryMapper.updateLogoutTime(userId);
    }

    //모든 접속 기록 조회
    public List<LogHistory> getAllLogs(){
        return logHistoryMapper.findAllLogs();
    }
}
