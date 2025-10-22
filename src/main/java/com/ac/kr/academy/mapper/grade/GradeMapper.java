package com.ac.kr.academy.mapper.grade;

import com.ac.kr.academy.domain.course.Course;
import com.ac.kr.academy.domain.grade.AlphabetSystem;
import com.ac.kr.academy.domain.grade.Grade;
import com.ac.kr.academy.domain.grade.GradeSystem;
import com.ac.kr.academy.domain.subject.Subject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

// 교수/학생 성적관리
@Mapper
public interface GradeMapper {

    /* 관리자 */
    // 전체 점수 규정 조회
    List<AlphabetSystem> findAlphabetGlobal(@Param("start") int start,
                                            @Param("end") int end,
                                            @Param("searchType") String searchType,
                                            @Param("searchKeyword") String searchKeyword
    );

    // 전체 건수 조회
    long countAlphabetGlobal(
            @Param("searchType") String searchType,
            @Param("searchKeyword") String searchKeyword
    );

    // 특정 과목 규정 조회
    List<AlphabetSystem> findAlphabetBySubject(@Param("subjectId") Long subjectId,
                                               @Param("enrollmentId") Long enrollmentId,
                                               @Param("start") int start,
                                               @Param("end") int end);

    // 과목/수강별 등급 규정 총 건수
    long countAlphabetSubject(@Param("subjectId") Long subjectId,
                              @Param("enrollmentId") Long enrollmentId);

    // 과목/수강 전용 규정 전체 (경계값 내림차순)
    List<AlphabetSystem> listAlphabetBySubjectAll(
            @Param("subjectId") Long subjectId,
            @Param("enrollmentId") Long enrollmentId
    );


    // 수강ID로 코스ID 조회
    Long findCourseIdByEnrollment(@Param("enrollmentId") Long enrollmentId);

    // 수강ID로 학생ID 조회
    Long findStudentIdByEnrollment(@Param("enrollmentId") Long enrollmentId);

    // 수강ID로 과목ID 조회
    Long findSubjectIdByEnrollment(@Param("enrollmentId") Long enrollmentId);

    // 강의ID로 과목ID 조회
    Long findSubjectIdByCourse(@Param("courseId") Long courseId);

    // 과목ID로 강의ID 목록 조회
    List<Long> findCourseIdsBySubject(@Param("subjectId") Long subjectId);

    // 강의의 모든 성적 조회
    List<Grade> findAllGradesByCourse(@Param("courseId") Long courseId);


    // 과목 규정 추가
    int insertAlphabetBySubject(AlphabetSystem rule);

    // 규정 수정
    int updateAlphabetRule(AlphabetSystem rule);

    // 규정 삭제
    int deleteAlphabetRule(@Param("id") Long id);


    // 특정 과목의 모든 규정 삭제
    int deleteAlphabetBySubject(@Param("subjectId") Long subjectId);

    // (정리됨)

    /* 교수 */

    // 성적이 없는 학생들 조회
    List<Map<String, Object>> findStudentsWithoutGrade(@Param("courseId") Long courseId);

    // 수업별 점수분배 비율 조회
    List<GradeSystem> findGradeSystemByCoursePaged(@Param("courseId") Long courseId,
                                                   @Param("start") int start,
                                                   @Param("end") int end);

    // 과목별 성적비율 총 개수
    long countGradeSystemByCourse(@Param("courseId") Long courseId);

    // 점수 분배 비율 추가
    int insertGradeSystem(GradeSystem system);

    // 점수 분배 비율 수정
    int updateGradeSystem(GradeSystem system);

    GradeSystem getGradeSystemById(Long id);

    // 특정 수업 성적 총 건수
    long countByCourse(@Param("professorId") Long professorId,
                       @Param("courseId") Long courseId,
                       @Param("subjectId") Long subjectId,
                       @Param("searchType") String searchType,
                       @Param("searchKeyword") String searchKeyword);

    // 수업 성적 목록 조회
    List<Grade> findByCourse(@Param("professorId") Long professorId,
                             @Param("courseId") Long courseId,
                             @Param("subjectId") Long subjectId,
                             @Param("searchType") String searchType,
                             @Param("searchKeyword") String searchKeyword,
                             @Param("start") int start,
                             @Param("end") int end
    );

    // 성적 등록
    void insert(Grade grade);

    // 성적 수정
    void update(Grade grade);

    // 성적 삭제
    void delete(@Param("id") Long id);



    /* ========== 권한체크 ========== */
    int existsGradeForProfessor(@Param("gradeId") Long gradeId,
                                @Param("professorId") Long professorId);

    /*==================================학생================================*/


    // 성적 단건 조회
    List<Grade> findMyGradeById(@Param("id") Long id,
                                @Param("studentId") Long studentId);

    // 집계: 전체 취득학점/평균 GPA (gpa는 ×10 저장이므로 /10 필요)
    Long sumScoreByStudent(@Param("studentId") Long studentId);
    Long avgGpa10ByStudent(@Param("studentId") Long studentId);

    // 학생 수강신청 목록 총 건수 (성적 등록 여부 포함)
    long countMyEnrollmentsWithGrades(@Param("studentId") Long studentId,
                                      @Param("searchType") String searchType,
                                      @Param("searchKeyword") String searchKeyword);

    // 학생 수강신청 목록 조회 (성적 등록 여부 포함)
    List<Map<String, Object>> findMyEnrollmentsWithGrades(@Param("studentId") Long studentId,
                                                          @Param("searchType") String searchType,
                                                          @Param("searchKeyword") String searchKeyword,
                                                          @Param("start") int start,
                                                          @Param("end") int end);

    // 학기별 성적 조회 (1~8학기)
    List<Map<String, Object>> findMyGradesBySemester(@Param("studentId") Long studentId);

    // 학기별 상세 성적 조회 (특정 학기의 과목별 성적)
    List<Map<String, Object>> findMyGradesBySemesterDetail(@Param("studentId") Long studentId, @Param("semesterId") Long semesterId);


    // 퍼센트 기반 학점 분배를 위한 통계 메서드들
    Long countStudentsByEnrollment(@Param("enrollmentId") Long enrollmentId);
    Long countStudentsWithHigherScore(@Param("enrollmentId") Long enrollmentId, @Param("score") int score);
    Long countStudentsWithLowerScore(@Param("enrollmentId") Long enrollmentId, @Param("score") int score);

    // 글로벌 규정 관리
    void deleteAlphabetGlobal();
    void insertAlphabetGlobal(AlphabetSystem rule);

    /*==================================공통================================*/
    //성적 단건 조회
    Grade findById(@Param("id") Long id);
    
    // 수강ID로 성적 조회
    Grade findByEnrollmentId(@Param("enrollmentId") Long enrollmentId);


    // Alphabet 규정 조회 (글로벌)
    List<AlphabetSystem> findGlobalAlphabetRules();


    // 점수 배율 조회 (단건)
    GradeSystem findGradeSystemByCourse(@Param("courseId") Long courseId);

    // 강의 정보 조회
    Course findCourseById(@Param("courseId") Long courseId);


    // 과목별 규정 상태 조회
    List<Map<String, Object>> findSubjectRulesStatus(@Param("searchType") String searchType,
                                                     @Param("searchKeyword") String searchKeyword,
                                                     @Param("start") int start,
                                                     @Param("end") int end);

    // 과목별 규정 상태 총 건수
    long countSubjectRules(@Param("searchType") String searchType,
                           @Param("searchKeyword") String searchKeyword);


    // 특정 강의의 수강생 수 조회
    Long countStudentsByCourse(@Param("courseId") Long courseId);

    // 학생 정보 조회 (성적용) - 원래는 StudentMapper에 있어야 하는 메서드
    Map<String, Object> findStudentInfoForGrade(@Param("studentId") Long studentId);

    // (정리됨)
}
