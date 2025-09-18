package com.ac.kr.academy.mapper.grade;

import com.ac.kr.academy.domain.grade.Grade;
import com.ac.kr.academy.dto.page.PageRequestDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 교수 성적관리 , 학생 성적관리
@Mapper
public interface GradeMapper {

    // 교수
    // 특정 수업(course) 성적 총 건수
    long countByCourse(@Param("courseId") Long courseId,
                       @Param("professorId") Long professorId);
}
