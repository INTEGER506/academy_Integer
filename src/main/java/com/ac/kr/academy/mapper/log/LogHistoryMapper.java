package com.ac.kr.academy.mapper.log;

import com.ac.kr.academy.domain.log.LogHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LogHistoryMapper {
    void insertLoginLog(LogHistory logHistory);

    //로그아웃 시간
    void updateLogoutTime(@Param("userId") Long userId);

    //모든 접속 기록 조회
    List<LogHistory> findAllLogs();
}
