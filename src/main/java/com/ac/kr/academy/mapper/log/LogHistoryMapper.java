package com.ac.kr.academy.mapper.log;

import com.ac.kr.academy.domain.log.LogHistory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogHistoryMapper {
    void insertLoginLog(LogHistory logHistory);

    //로그아웃 시간
    void updateLogoutTime(Long userId);
}
