package com.ac.kr.academy.mapper.semester;


import com.ac.kr.academy.domain.semester.Semester;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SemesterMapper {

    void insert(Semester semester);

    Semester findById(Long id);

    List<Semester> findAll();

    void update(Semester semester);

    void deleteById(Long id);

}
