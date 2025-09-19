package com.ac.kr.academy.service.grade;


import com.ac.kr.academy.domain.grade.Grade;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GradeService {

    /*================================교수================================*/
    // 강의(Course)별 성적 목록
    List<Grade> findByCourseAll(@Param("professorId") Long professorId, @Param("courseId") Long courseId);

    //성적 등록
    void addGrade(Grade grade, Long professorId);

    //성적 수정
    void editGrade(Grade grade, Long professorId);

    //성적 삭제
    void deleteGrade(Long id, Long professorId);

    /*================================학생================================*/
    // 성적 목록
    List<Grade> findMyGrade(@Param("studentId") Long studentId,
                            @Param("semesterId") Long semesterId,
                            @Param("subjectId") Long subjectId);

    // 단건 조회
    Grade findMyGradeById(@Param("id") Long id,
                          @Param("studentId") Long studentId);

    /*================================공통================================*/
    // 성적 단건 조회
    Grade findGrade(Long id);
}
