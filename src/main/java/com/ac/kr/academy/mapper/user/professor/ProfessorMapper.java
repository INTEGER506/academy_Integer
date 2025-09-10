package com.ac.kr.academy.mapper.user.professor;

import com.ac.kr.academy.domain.user.Professor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProfessorMapper {

    void insertProfessor(Professor professor);
    int updateProfessor(Professor professor);
    int deleteProfessor(@Param("id") Long id);

    List<Professor> findAllProfessors();
    Professor findById(@Param("id") Long id);
    Professor findByProfessorNum(@Param("professorNum") String professorNum);
}
