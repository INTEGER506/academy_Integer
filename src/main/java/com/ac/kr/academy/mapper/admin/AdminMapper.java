package com.ac.kr.academy.mapper.admin;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 로그인 ID 자동생성 매퍼
 * ID 순번을 먼저 올린 후, 변경된 순번을 다시 읽어오는 역할 -> 순차적으로 순번을 증가
 * 두 메서드를 하나의 트랜잭션으로 묶어 실행할 예정 -> AdminRestController
 */

@Mapper
public interface AdminMapper {
    void incrementSequence(@Param("sequenceKey") String sequenceKey);   //ID 순번 증가
    int findSequenceNum(@Param("sequenceKey") String sequenceKey);      //ID 순번 조회
}
