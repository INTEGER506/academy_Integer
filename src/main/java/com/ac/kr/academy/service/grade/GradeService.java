package com.ac.kr.academy.service.grade;


import com.ac.kr.academy.domain.course.Course;
import com.ac.kr.academy.domain.grade.AlphabetSystem;
import com.ac.kr.academy.domain.grade.Grade;
import com.ac.kr.academy.domain.grade.GradeSystem;
import com.ac.kr.academy.domain.subject.Subject;
import com.ac.kr.academy.dto.grade.SubjectRuleDTO;
import com.ac.kr.academy.dto.page.PageRequestDTO;
import com.ac.kr.academy.dto.page.PageResponseDTO;
import org.apache.ibatis.annotations.Param;


import java.util.List;
import java.util.Map;
import com.ac.kr.academy.mapper.grade.GradeMapper;

public interface GradeService {
    
    // 디버깅용 매퍼 접근
    GradeMapper getGradeMapper();

    /*================================관리자================================*/
    // 글로벌 규정 조회
    PageResponseDTO<AlphabetSystem> listAlphabetGlobal(@Param("searchType") String searchType,
                                                       @Param("searchKeyword") String searchKeyword,
                                                       @Param("req") PageRequestDTO req);

    // 글로벌 규정 등록

    // 특정 과목 규정 조회
    PageResponseDTO<AlphabetSystem> listAlphabetBySubject(Long subjectId,
                                                          Long enrollmentId,
                                                          @Param("searchType") String searchType,
                                                          @Param("searchKeyword") String searchKeyword,
                                                          PageRequestDTO req);

    // 규정 추가
    void addAlphabetRule(AlphabetSystem as);

    // 규정 수정
    void updateAlphabetRule(AlphabetSystem as);

    // 규정 삭제
    void deleteAlphabetRule(@Param("id") Long id);



    // ================== 과목별 규정 ==================
    // 모든 과목 조회
    List<Subject> getAllSubjects();


    // 성적이 없는 학생들 조회 (성적 등록용)
    List<Map<String, Object>> getStudentsWithoutGrade(Long courseId);


    /*================================교수================================*/
    // 교수가 개설한 강의 목록 조회
    PageResponseDTO<Map<String, Object>> listProfessorCourses(Long professorId, 
                                                              String searchType, 
                                                              String searchKeyword, 
                                                              PageRequestDTO req);

    // 점수 비율 조회
    PageResponseDTO<GradeSystem> listGradeSystemByCourse(@Param("courseId") Long courseId,
                                                         @Param("searchType") String searchType,
                                                         @Param("searchKeyword") String searchKeyword,
                                                         PageRequestDTO req);

    // 점수분배 등록
    void addGradeSystem(GradeSystem gs);

    // 점수분배 수정
    void editGradeSystem(GradeSystem gs);
    
    // 점수분배 조회
    GradeSystem getGradeSystemById(Long id);

    // 성적 목록 조회 (검색 / 페이징)
    PageResponseDTO<Grade> listByCourse(@Param("professorId") Long professorId,
                                        @Param("courseId") Long courseId,
                                        @Param("subjectId") Long subjectId,
                                        @Param("searchType") String searchType,
                                        @Param("searchKeyword") String searchKeyword,
                                        PageRequestDTO req);

    // 성적 등록
    void addGrade(Grade grade, Long professorId);

    // 성적 수정
    void editGrade(Grade grade, Long professorId);

    // 성적 삭제
    void deleteGrade(Long gradeId, Long professorId);

    // 기본 글로벌 알파벳 규정 조회
    List<AlphabetSystem> getDefaultAlphabetRules();

    GradeSystem getGradeSystemByCourse(Long courseId);

    Map<String, Object> calculateGradePreview(Grade grade);


    // Alphabet 규정 조회 (과목별)
    List<AlphabetSystem> findAlphabetBySubject(Long courseId);



    /*================================학생================================*/

    // 학생 수강신청 목록 조회 (성적 등록 여부 포함)
    PageResponseDTO<Map<String, Object>> listMyEnrollmentsWithGrades(@Param("studentId") Long studentId,
                                                                     @Param("searchType") String searchType,
                                                                     @Param("searchKeyword") String searchKeyword,
                                                                     @Param("req") PageRequestDTO req);

    // 학생 성적 단건 조회(상세조회)
    Grade getMyGrade(Long studentId, Long id);

    // 학기별 성적 조회 (1~8학기)
    PageResponseDTO<Map<String, Object>> listMyGradesBySemester(Long studentId);

    // 학기별 상세 성적 조회 (특정 학기의 과목별 성적)
    PageResponseDTO<Map<String, Object>> listMyGradesBySemesterDetail(Long studentId, Long semesterId);

    /*================================공통================================*/
    // 성적 단건 조회
    Grade findGrade(Long id);

    // ================== 집계/졸업 ==================
    Map<String, Object> summarizeForStudent(Long studentId);
    Map<String, Object> checkGraduation(Long studentId, long requiredScore, double requiredAvgGpa);

    // ================== 점수 분배 비율 관리 ==================
    // 강의 정보 조회
    Course getCourseById(Long courseId);
    
    // ================== 과목별 규정 관리 ==================
    // 과목별 규정 상태 조회 (글로벌 vs 커스텀)
    PageResponseDTO<Map<String, Object>> listSubjectRulesStatus(String searchType, 
                                                               String searchKeyword, 
                                                               PageRequestDTO req);
    
    // 글로벌 규정 저장
    void saveGlobalRules(Map<String, String> params);
    
    // 전체 학생 학점 재계산 (상대평가용)
    void recalculateAllGradesForCourse(Long courseId);
    
    // 과목별 규정 초기화 (글로벌 규정으로 되돌리기)
    void resetSubjectToGlobal(Long subjectId);
    
    // 과목별 규정 인라인 저장
    void saveSubjectRulesInline(Map<String, String> params);
    
    // 글로벌 규정을 복사하여 커스텀 규정 생성
    void createCustomRulesFromGlobal(Long subjectId);
    
}
