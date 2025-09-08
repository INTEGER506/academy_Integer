package com.ac.kr.academy.mapper.professor;


import com.ac.kr.academy.domain.professor.Professor;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProfessorMapper {
    void insert(Professor professor);

    Professor findById(Long id);

    void update(Professor professor);

    void deleteById(Long id);
}
