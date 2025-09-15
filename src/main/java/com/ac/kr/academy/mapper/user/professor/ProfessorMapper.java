package com.ac.kr.academy.mapper.user.professor;

import com.ac.kr.academy.domain.user.Professor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProfessorMapper {

    void insertProfessor(Professor professor);
    int updateProfessor(Professor professor);
    int deleteProfessor(@Param("id") Long id);

    Professor findByUserId(@Param("userId") Long userId);
}
