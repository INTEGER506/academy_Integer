package com.ac.kr.academy.service.grade;


import com.ac.kr.academy.domain.grade.AlphabetSystem;
import com.ac.kr.academy.domain.grade.Grade;
import com.ac.kr.academy.domain.grade.GradeSystem;
import com.ac.kr.academy.dto.page.PageRequestDTO;
import com.ac.kr.academy.dto.page.PageResponseDTO;
import com.ac.kr.academy.mapper.grade.GradeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {

    private final GradeMapper gradeMapper;


    /*================================관리자================================*/

    // 전체 규정 목록
    @Override
    public PageResponseDTO<AlphabetSystem> listAlphabetGlobal(String searchType,
                                                              String searchKeyword,
                                                              PageRequestDTO req) {
        long total = gradeMapper.countAlphabetGlobal(searchType, searchKeyword);
        List<AlphabetSystem> rows = (total == 0)
                ? Collections.emptyList()
                : gradeMapper.findAlphabetGlobal(req.getStart(), req.getEnd(), searchType, searchKeyword);
        return PageResponseDTO.pageOf(rows, total, req);
    }

    // 과목/수강 규정 목록
    @Override
    public PageResponseDTO<AlphabetSystem> listAlphabetBySubject(Long subjectId,
                                                                 Long enrollmentId,
                                                                 String searchType,
                                                                 String searchKeyword,
                                                                 PageRequestDTO req) {
        long total = gradeMapper.countAlphabetSubject(subjectId, enrollmentId);
        List<AlphabetSystem> rows = (total == 0)
                ? Collections.emptyList()
                : gradeMapper.findAlphabetBySubject(
                                                    subjectId,
                                                    enrollmentId,
                                                    req.getStart(),
                                                    req.getEnd()
                                                    );
        return PageResponseDTO.pageOf(rows, total, req);
    }

    // 규정 추가
    @Transactional
    @Override
    public void addAlphabetGlobal(AlphabetSystem as) { gradeMapper.insertAlphabetGlobal(as); }

    // 규정 수정
    @Override
    @Transactional
    public void updateAlphabetRule(AlphabetSystem as) { gradeMapper.updateAlphabetRule(as); }

    // 규정 삭제
    @Override
    @Transactional
    public void deleteAlphabetRule(Long id) {
        gradeMapper.deleteAlphabetRule(id);
    }

    /*================================교수================================*/

    // 교수 성적 목록
    @Override
    public PageResponseDTO<Grade> listByCourse(Long professorId,
                                               Long courseId,
                                               Long subjectId,
                                               String searchType,
                                               String searchKeyword,
                                               PageRequestDTO req) {

        long total = gradeMapper.countByCourse(professorId, courseId, subjectId, searchType, searchKeyword);

        List<Grade> list = (total == 0)
                ? Collections.emptyList()
                : gradeMapper.findByCourse(professorId, courseId, subjectId,
                searchType, searchKeyword,
                req.getStart(), req.getEnd());

        return PageResponseDTO.pageOf(list, total, req);
    }

    // 성적 등록
    @Transactional
    @Override
    public void addGrade(Grade grade, Long professorId) {
        applyCalcWithRules(grade, null, null);
        gradeMapper.insert(grade);
    }

    // 성적 수정
    @Override
    @Transactional
    public void editGrade(Grade grade, Long professorId) {
        applyCalcWithRules(grade, null, null);
        gradeMapper.update(grade);
    }

    // 성적 삭제
    @Override
    @Transactional
    public void deleteGrade(Long gradeId, Long professorId) {
        gradeMapper.delete(gradeId);
    }

    // 점수 분배
    @Override
    public PageResponseDTO<GradeSystem> listGradeSystemByCourse(Long courseId,
                                                                String searchType,
                                                                String searchKeyword,
                                                                PageRequestDTO req) {

        long total = gradeMapper.countGradeSystemByCourse(courseId);
        List<GradeSystem> rows = (total == 0)
                ? Collections.emptyList()
                : gradeMapper.findGradeSystemByCourse(courseId, req.getStart(), req.getEnd());

        return PageResponseDTO.pageOf(rows, total, req);
    }

    // 점수 분배 등록
    @Transactional
    @Override
    public void addGradeSystem(GradeSystem gs) { gradeMapper.insertGradeSystem(gs); }

    // 점수 분배 수정
    @Override
    @Transactional
    public void editGradeSystem(GradeSystem gs) { gradeMapper.updateGradeSystem(gs); }

    @Override
    public GradeSystem getGradeSystemByCourse(Long courseId) {
        if (courseId == null) return null;
        // 코스별 점수비율 1건 조회 (이미 Mapper에 있음)
        return gradeMapper.findGradeSystemOne(courseId);
    }

    @Override
    public Map<String, Object> calculateGradePreview(Grade grade) {
        if (grade == null) return Map.of();

        // 저장하지 않고, 기존 계산 로직만 재사용해서 결과만 반환
        Long enrollmentId = grade.getEnrollmentId(); // 과목 전용 규정이 필요하면 Controller에서 함께 넘겨줄 수 있음
        // subjectId를 Grade에 두지 않았다면 null로 둠 (전용 규정 없으면 글로벌 규정 fallback)
        Long subjectId = null;

        // 기존 내부 계산 파이프라인 그대로 사용
        applyCalcWithRules(grade, subjectId, enrollmentId);

        Map<String, Object> result = new HashMap<>();
        result.put("totalScore", grade.getTotalInt());
        result.put("alphabet",   grade.getAlphabet());
        result.put("gpa10",      grade.getGpa());   // 4.5 → 45 형태(×10)

        return result;
    }


    /*================================학생================================*/
    // 학생 성적 조회
    @Override
    public PageResponseDTO<Grade> listMyGrades(Long studentId,
                                               String searchType,
                                               String searchKeyword,
                                               PageRequestDTO req) {


        long total = gradeMapper.countMyGrade(studentId, searchType, searchKeyword);

        List<Grade> list = (total == 0)
                ? Collections.emptyList()
                : gradeMapper.findMyGrade(
                studentId, searchType, searchKeyword, req.getStart(),  req.getEnd()
        );

        return PageResponseDTO.pageOf(list, total, req);
    }

    // 성적 단건 조회
    @Override
    public Grade getMyGrade(Long studentId, Long id) {
        return (Grade) gradeMapper.findMyGradeById(id, studentId);
    }

    /*    =========================================================================================================================*/
    @Override
    public Grade findGrade(Long id) {
        return gradeMapper.findById(id);
    }


    @Override
    public void addAlphabetRule(AlphabetSystem rule) {

    }

    /* ================= 계산용: 규칙 로딩 + 산출 ================= */

    private void applyCalcWithRules(Grade grade, Long subjectId, Long enrollmentId) {
        // 1) courseId 확보 (enrollmentId 기준)
        Long courseId = null;
        if (grade.getEnrollmentId() != null) {
            courseId = gradeMapper.findCourseIdByEnrollment(grade.getEnrollmentId());
        }

        // 2) 코스별 점수비율 1건 (없으면 null → 기본 25% 사용)
        GradeSystem gs = (courseId != null) ? gradeMapper.findGradeSystemOne(courseId) : null;

        // 3) 등급 규정(as): 과목/수강 전용 → 없으면 글로벌
        List<AlphabetSystem> rulesAs = null;
        if (subjectId != null || enrollmentId != null) {
            rulesAs = gradeMapper.listAlphabetBySubjectAll(subjectId, enrollmentId); // All (A L L 아님)
        }
        if (rulesAs == null || rulesAs.isEmpty()) {
            rulesAs = gradeMapper.listAlphabetGlobalAll(); // All (A L L 아님)
        }

        // 4) 실제 계산
        recalcWithRules(grade, gs, rulesAs);

    }

    private int i(Integer v) { return v == null ? 0 : v; }

    private void recalcWithRules(Grade grade, GradeSystem gs, List<AlphabetSystem> rulesAs) {
        // gs의 비율(Double%) → int% (없으면 25)
        int midR = (gs != null && gs.getMidExamRatio()    != null) ? (int) Math.round(gs.getMidExamRatio())    : 25;
        int finR = (gs != null && gs.getFinalExamRatio()  != null) ? (int) Math.round(gs.getFinalExamRatio())  : 25;
        int asgR = (gs != null && gs.getAssignmentRatio() != null) ? (int) Math.round(gs.getAssignmentRatio()) : 25;
        int attR = (gs != null && gs.getAttendanceRatio() != null) ? (int) Math.round(gs.getAttendanceRatio()) : 25;

        //여기는 정수 곱셈만 — Math.round(int) 같은 잘못된 호출 제거
        int sum = i(grade.getMidExam())    * midR
                + i(grade.getFinalExam())  * finR
                + i(grade.getAssignment()) * asgR
                + i(grade.getAttendance()) * attR;

        int totalInt = (sum + 50) / 100;      // HALF_UP 등가
        grade.setTotalInt(totalInt);
        grade.setScore((long) totalInt);      // score: Long

        // 규정 적용 (boundary DESC에서 첫 매칭)
        String alphabet = "F";
        if (rulesAs != null) {
            for (AlphabetSystem as : rulesAs) {
                Double bd = as.getBoundary();
                if (bd == null) continue;
                if (totalInt >= bd.intValue()) {  // 또는 (double)totalInt >= bd
                    alphabet = as.getAlphabet();
                    break;
                }

            }
        }
        grade.setAlphabet(alphabet);
        grade.setGpa(mapAlphabetToGpa10(alphabet)); // gpa: Long (4.5 → 45)
    }

    // A+, A, B+, B, C+, C, D+, D, F → GPA×10
    private long mapAlphabetToGpa10(String a) {
        if (a == null) return 0L;
        switch (a) {
            case "A+": return 45L;
            case "A":  return 40L;
            case "B+": return 35L;
            case "B":  return 30L;
            case "C+": return 25L;
            case "C":  return 20L;
            case "D+": return 15L;
            case "D":  return 10L;
            default:   return 0L; // F
        }
    }


}
