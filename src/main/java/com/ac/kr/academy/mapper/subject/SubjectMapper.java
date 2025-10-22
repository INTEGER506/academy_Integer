package com.ac.kr.academy.mapper.subject;


import com.ac.kr.academy.domain.subject.Subject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SubjectMapper {

    void insert(Subject subject);

    Subject findById(Long id);

    List<Subject> findAll();

    void update(Subject subject);

    void deleteById(Long id);

    //특정 과목의 학점(credit)을 조회
    Integer findCreditBySubjectId(@Param("subjectId") Long subjectId);
}
