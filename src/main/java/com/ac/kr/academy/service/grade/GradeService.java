package com.ac.kr.academy.service.grade;


import com.ac.kr.academy.domain.grade.AlphabetSystem;
import com.ac.kr.academy.domain.grade.Grade;
import com.ac.kr.academy.domain.grade.GradeSystem;
import com.ac.kr.academy.dto.page.PageRequestDTO;
import com.ac.kr.academy.dto.page.PageResponseDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GradeService {

    /*================================관리자================================*/
    // 글로벌 규정 조회
    PageResponseDTO<AlphabetSystem> listAlphabetGlobal(PageRequestDTO pageRequestDTO);

    // 글로벌 규정 등록
    void addAlphabetGlobal(AlphabetSystem rule);

    // 특정 과목 규정 조회
    List<AlphabetSystem> getAlphabetBySubject(@Param("id") Long subjectId);

    // 규정 추가
    void addAlphabetRule(AlphabetSystem rule, boolean global);

    // 규정 수정
    void updateAlphabetRule(AlphabetSystem rule);

    // 규정 삭제
    void deleteAlphabetRule(@Param("id") Long id);

    /*================================교수================================*/
    // 점수 비율 조회
    GradeSystem getGradeSystem(Long courseId, Long professorId);

    // 점수 비율 입력
    void setGradeSystem(Long courseId, Long professorId);

    // 성적 목록 조회 (검색 / 페이징)
    PageResponseDTO<Grade> listByCourse(@Param("courseId") Long courseId,
                                        @Param("searchType") String searchType,         // "n"= 학생명 "s"= 과목명
                                        @Param("searchKeyword") String searchKeyword,
                                        @Param("sort") String sort,
                                        PageRequestDTO pageRequestDTO,
                                        @Param("professorId") Long professorId);

    // 성적 등록
    void addGrade(Grade grade, Long professorId);

    // 성적 수정
    void editGrade(Grade grade, Long professorId);

    // 성적 삭제
    void deleteGrade(Long gradeId, Long professorId);
    /*================================학생================================*/
    // 학생 성적 목록 조회 (페이징 / 검색)
    PageResponseDTO<Grade> listMyGrades(@Param("studentId") Long studentId,
                                        @Param("searchType") String searchType,
                                        @Param("searchKeyword") String searchKeyword,
                                        @Param("sort") String sort,
                                        PageRequestDTO pageRequestDTO);

    // 학생 성적 단건 조회(상세조회)
    Grade getMyGrade(Long studentId, Long id);
    /*================================공통================================*/
    // 성적 단건 조회
    Grade findGrade(Long id);
}
