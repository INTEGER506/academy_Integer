package com.ac.kr.academy.mapper.log;

import com.ac.kr.academy.domain.log.Log;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogMapper {
    void insertLog(Log log);

}
