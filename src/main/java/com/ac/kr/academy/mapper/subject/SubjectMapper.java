package com.ac.kr.academy.mapper.subject;


import com.ac.kr.academy.domain.subject.Subject;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SubjectMapper {

    void insert(Subject subject);

    Subject findById(Long id);

    List<Subject> findAll();

    void update(Subject subject);

    void deleteById(Long id);
}
