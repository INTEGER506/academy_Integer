package com.ac.kr.academy.mapper.grade;

import com.ac.kr.academy.domain.grade.AlphabetSystem;
import com.ac.kr.academy.domain.grade.Grade;
import com.ac.kr.academy.domain.grade.GradeSystem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

// 교수 성적관리 , 학생 성적관리
@Mapper
public interface GradeMapper {

    /*==================================관리자================================*/
    // 전체 점수 규정 조회 (subject_id IS NULL)
    List<AlphabetSystem> findAlphabetGlobal(@Param("start") int start,
                                            @Param("end") int end);

    // 특정 과목 규정 조회 (subject_id = ?)
    List<AlphabetSystem> findAlphabetBySubject(@Param("subjectId") Long subjectId,
                                               @Param("enrollmentId") Long enrollmentId,
                                               @Param("start") int start,
                                               @Param("end") int end);

    // 과목/ 수강별 등급 규정 총 건수
    long countAlphabetSubject(@Param("subjectId") Long subjectId,
                              @Param("enrollmentId") Long enrollmentId);

    // 글로벌 규정 추가 (subject_id = Null)
    int insertAlphabetGlobal(AlphabetSystem rule);

    // 과목 규정 추가 (subject_id = #{subjectId})
    int insertAlphabetBySubject(AlphabetSystem rule);

    // 규정 수정 (id 기준)
    int updateAlphabetRule(AlphabetSystem rule);

    // 규정 삭제 (id 기준)
    int deleteAlphabetRule(@Param("id") Long id);

    /*==================================교수================================*/
    // 수업별 점수분배 비율 조회 (없으면 서비스에서 디폴트값으로 적용)
    GradeSystem findGradeSystemByCourse(@Param("courseId") Long courseId);

    // 점수 분배 비율 작성
    int insertGradeSystem(GradeSystem GradeSystem);

    // 점수 분배 비율 수정
    int updateGradeSystem(GradeSystem GradeSystem);

    // 특정 수업(course) 성적 총 건수
    long countByCourse(@Param("courseId") Long courseId,
                       @Param("professorId") Long professorId,
                       @Param("subjectId") Long subjectId,
                       @Param("searchType") String searchType,
                       @Param("searchKeyword") String keyword);

    // 수업 성적 목록 조회
    List<Grade> findByCourse(
            @Param("courseId") Long courseId,
            @Param("professorId") Long professorId,
            @Param("subjectId") Long subjectId,
            @Param("searchKeyword") String searchKeyword,
            @Param("searchType") String searchType,
            @Param("start") int start,
            @Param("end") int end
    );

    // 성적 등록
    void insert(Grade grade, Long professorId);

    // 성적 수정
    void update(Grade grade, Long professorId);

    // 성적 삭제
    void delete(@Param("id") Long id, Long professorId);

    // 개설한 수강 = 교수 검사
    int AccessEnrollment(@Param("enrollment") Long enrollment, @Param("professorId") Long professorId);

    // 본인이 부여한 성적이 맞는지 검사
    int isOwnerOfGrade(@Param("gradeId") Long gradeId,
                       @Param("professorId") Long professorId);

    /*==================================학생================================*/
    // 학생 성적 총 건수
    long countMyGrade(@Param("studentId") Long studentId,
                      @Param("searchType") String searchType,
                      @Param("keyword") String searchKeyword);

    // 성적 조회
    List<Grade> findMyGrade(@Param("studentId") Long studentId,
                            @Param("searchType") String searchType,
                            @Param("searchKeyword") String searchKeyword,
                            @Param("start") int start,
                            @Param("end") int end);

    // 성적 단건 조회
    List<Grade> findMyGradeById(@Param("id") Long id,
                                @Param("studentId") Long studentId);

    /*==================================공통================================*/
    //성적 단건 조회
    Grade findById(@Param("id") Long id);

}
