package com.ac.kr.academy.mapper.user.professor;

import com.ac.kr.academy.domain.user.Professor;
import com.ac.kr.academy.dto.user.MyPageInfoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProfessorMapper {

    void insertProfessor(Professor professor);
    int updateProfessor(Professor professor);
    int deleteProfessor(@Param("id") Long userId);

    Professor findByUserId(@Param("userId") Long userId);

    //권한변경을 위한 추가 메서드 (교수 -> 지도교수)
    Long findProfessorIdByUserId(@Param("userId") Long userId);

    //마이페이지용
    MyPageInfoDTO findMyPageInfoByUserId(@Param("userId") Long userId);
}