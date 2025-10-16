package com.ac.kr.academy.service.grade;


import com.ac.kr.academy.domain.course.Course;
import com.ac.kr.academy.domain.grade.AlphabetSystem;
import com.ac.kr.academy.domain.grade.Grade;
import com.ac.kr.academy.domain.grade.GradeSystem;
import com.ac.kr.academy.domain.subject.Subject;
import com.ac.kr.academy.dto.grade.SubjectRuleDTO;
import com.ac.kr.academy.dto.page.PageRequestDTO;
import com.ac.kr.academy.dto.page.PageResponseDTO;
import com.ac.kr.academy.mapper.grade.GradeMapper;
import com.ac.kr.academy.mapper.subject.SubjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.Arrays;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {

    private final GradeMapper gradeMapper;
    private final SubjectMapper subjectMapper;


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
        enforceProfessorOwnershipIfNeeded(grade.getId(), professorId, grade.getEnrollmentId());
        applyCalcWithRules(grade, null, null);
        gradeMapper.insert(grade);
    }

    // 성적 수정
    @Override
    @Transactional
    public void editGrade(Grade grade, Long professorId) {
        enforceProfessorOwnershipIfNeeded(grade.getId(), professorId, grade.getEnrollmentId());
        applyCalcWithRules(grade, null, null);
        gradeMapper.update(grade);
    }

    // 성적 삭제
    @Override
    @Transactional
    public void deleteGrade(Long gradeId, Long professorId) {
        if (professorId != null) {
            int own = gradeMapper.existsGradeForProfessor(gradeId, professorId);
            if (own == 0) throw new IllegalStateException("not owner of this grade");
        }
        gradeMapper.delete(gradeId);
    }

    // 교수가 개설한 강의 목록 조회
    @Override
    public PageResponseDTO<Map<String, Object>> listProfessorCourses(Long professorId, 
                                                                   String searchType, 
                                                                   String searchKeyword, 
                                                                   PageRequestDTO req) {
        long total = gradeMapper.countProfessorCourses(professorId, searchType, searchKeyword);
        List<Map<String, Object>> rows = (total == 0)
                ? Collections.emptyList()
                : gradeMapper.findProfessorCourses(professorId, searchType, searchKeyword, req.getStart(), req.getEnd());

        return PageResponseDTO.pageOf(rows, total, req);
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
                : gradeMapper.findGradeSystemByCoursePaged(courseId, req.getStart(), req.getEnd());

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
        return gradeMapper.findGradeSystemByCourse(courseId);
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

    @Override
    public Map<String, Object> summarizeForStudent(Long studentId) {
        if (studentId == null) return Map.of();
        Long sumScore = gradeMapper.sumScoreByStudent(studentId);
        Long avgGpa10 = gradeMapper.avgGpa10ByStudent(studentId);
        double avgGpa = (avgGpa10 == null ? 0 : avgGpa10) / 10.0;
        return Map.of(
                "totalScore", sumScore == null ? 0L : sumScore,
                "avgGpa", avgGpa
        );
    }

    @Override
    public Map<String, Object> checkGraduation(Long studentId, long requiredScore, double requiredAvgGpa) {
        Map<String, Object> sum = summarizeForStudent(studentId);
        long totalScore = ((Number) sum.getOrDefault("totalScore", 0L)).longValue();
        double avgGpa = ((Number) sum.getOrDefault("avgGpa", 0.0)).doubleValue();
        boolean ok = totalScore >= requiredScore && avgGpa >= requiredAvgGpa;
        return Map.of(
                "totalScore", totalScore,
                "avgGpa", avgGpa,
                "meets", ok
        );
    }

    private void enforceProfessorOwnershipIfNeeded(Long gradeId, Long professorId, Long enrollmentId) {
        if (professorId == null) return;
        if (gradeId != null) {
            int own = gradeMapper.existsGradeForProfessor(gradeId, professorId);
            if (own == 0) throw new IllegalStateException("not owner of this grade");
        }
        // insert 시에는 gradeId가 없으므로 최소한 enrollment→course를 통해 계산 시 소유권이 자연스레 적용됨
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
        // courseId가 있으면 과목 전용 규정으로 저장,
        // 없으면 글로벌 규정으로 저장
        if (rule == null) return;
        validateAlphabetRule(rule);
        if (rule.getCourseId() != null) {
            gradeMapper.insertAlphabetBySubject(rule);
        } else {
            gradeMapper.insertAlphabetGlobal(rule);
        }
    }

    private void validateAlphabetRule(AlphabetSystem rule) {
        // 허용 학점
        String a = rule.getAlphabet();
        if (a == null) throw new IllegalArgumentException("alphabet is required");
        switch (a) {
            case "A+": case "A": case "B+": case "B": case "C+": case "C": case "D+": case "D": case "F":
                break;
            default:
                throw new IllegalArgumentException("invalid alphabet: " + a);
        }
        Double b = rule.getBoundary();
        if (b == null) throw new IllegalArgumentException("boundary is required");
        if (b < 0.0 || b > 100.0) throw new IllegalArgumentException("boundary must be 0~100");
        
        // 중복 학점 체크 (같은 courseId에서 같은 alphabet 중복 방지)
    }

    @Override
    public Map<String, Double> getGlobalRulesMap() {
        List<AlphabetSystem> globalRules = gradeMapper.listAlphabetGlobalAll();
        Map<String, Double> rulesMap = new HashMap<>();
        
        for (AlphabetSystem rule : globalRules) {
            rulesMap.put(rule.getAlphabet(), rule.getBoundary());
        }
        
        // 기본값 설정 (없는 학점은 0으로)
        String[] grades = {"A+", "A", "B+", "B", "C+", "C", "D+", "D", "F"};
        for (String grade : grades) {
            rulesMap.putIfAbsent(grade, 0.0);
        }
        
        return rulesMap;
    }

    @Override
    public List<AlphabetSystem> getGlobalRulesAll() {
        return gradeMapper.listAlphabetGlobalAll();
    }

    @Override
    @Transactional
    public void saveGlobalRules(Map<String, String> params) {
        // 기존 글로벌 규정 모두 삭제
        gradeMapper.deleteAllGlobalRules();
        
        // 새로운 규정들 저장
        String[] grades = {"A+", "A", "B+", "B", "C+", "C", "D+", "D", "F"};
        for (String grade : grades) {
            String value = params.get(grade);
            if (value != null && !value.trim().isEmpty()) {
                try {
                    Double percentage = Double.parseDouble(value);
                    if (percentage >= 0 && percentage <= 100) {
                        AlphabetSystem rule = new AlphabetSystem();
                        rule.setAlphabet(grade);
                        rule.setBoundary(percentage);
                        rule.setCourseId(null); // 글로벌 규정
                        rule.setDescription("상위 " + percentage + "%");
                        gradeMapper.insertAlphabetGlobal(rule);
                    }
                } catch (NumberFormatException e) {
                    // 잘못된 숫자 형식은 무시
                }
            }
        }
    }

    @Override
    public List<Subject> getAllSubjects() {
        // Subject 매퍼에서 모든 과목 조회
        return subjectMapper.findAll();
    }

    @Override
    public Subject getSubjectById(Long subjectId) {
        return subjectMapper.findById(subjectId);
    }







    /* ================= 계산용: 규칙 로딩 + 산출 ================= */

    private void applyCalcWithRules(Grade grade, Long subjectId, Long enrollmentId) {
        // 1) courseId 확보 (enrollmentId 기준)
        Long courseId = null;
        if (grade.getEnrollmentId() != null) {
            courseId = gradeMapper.findCourseIdByEnrollment(grade.getEnrollmentId());
        }

        // 2) 코스별 점수비율 1건 (없으면 null → 기본 25% 사용)
        GradeSystem gs = (courseId != null) ? gradeMapper.findGradeSystemByCourse(courseId) : null;

        // 3) 등급 규정(as): 과목/수강 전용 → 없으면 글로벌
        List<AlphabetSystem> rulesAs = null;
        if (subjectId != null || enrollmentId != null) {
            rulesAs = gradeMapper.listAlphabetBySubjectAll(subjectId, enrollmentId); // All (A L L 아님)
        }
        if (rulesAs == null || rulesAs.isEmpty()) {
            rulesAs = gradeMapper.listAlphabetGlobalAll(); // All (A L L 아님)
        }

        // 4) 실제 계산
        recalcWithRules(grade, gs, rulesAs, enrollmentId);

    }

    private int i(Integer v) { return v == null ? 0 : v; }

    private void recalcWithRules(Grade grade, GradeSystem gs, List<AlphabetSystem> rulesAs, Long enrollmentId) {
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

        // 퍼센트 기반 학점 분배 적용
        String alphabet = calculateAlphabetByPercentage(totalInt, rulesAs, enrollmentId);
        grade.setAlphabet(alphabet);
        grade.setGpa(mapAlphabetToGpa10(alphabet)); // gpa: Long (4.5 → 45)
    }

    // 퍼센트 기반 학점 분배 계산
    private String calculateAlphabetByPercentage(int totalScore, List<AlphabetSystem> rules, Long enrollmentId) {
        if (rules == null || rules.isEmpty()) {
            return "F"; // 규정이 없으면 F
        }
        
        // 해당 수강의 전체 학생 수 조회
        Long totalStudents = gradeMapper.countStudentsByEnrollment(enrollmentId);
        if (totalStudents == null || totalStudents <= 0) {
            return "F"; // 학생이 없으면 F
        }
        
        // 해당 점수보다 높은 점수를 받은 학생 수 조회
        Long higherScoreCount = gradeMapper.countStudentsWithHigherScore(enrollmentId, totalScore);
        if (higherScoreCount == null) {
            higherScoreCount = 0L;
        }
        
        // 상위 몇 %인지 계산
        double percentile = ((double) higherScoreCount / totalStudents) * 100.0;
        
        // 규정을 내림차순으로 정렬 (A+부터 F까지)
        List<AlphabetSystem> sortedRules = new ArrayList<>(rules);
        sortedRules.sort((a, b) -> {
            String[] grades = {"A+", "A", "B+", "B", "C+", "C", "D+", "D", "F"};
            int indexA = Arrays.asList(grades).indexOf(a.getAlphabet());
            int indexB = Arrays.asList(grades).indexOf(b.getAlphabet());
            return Integer.compare(indexA, indexB);
        });
        
        // 누적 퍼센트로 학점 결정
        double cumulativePercent = 0.0;
        for (AlphabetSystem rule : sortedRules) {
            Double percentage = rule.getBoundary();
            if (percentage == null) continue;
            
            cumulativePercent += percentage;
            if (percentile <= cumulativePercent) {
                return rule.getAlphabet();
            }
        }
        
        return "F"; // 기본값
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

    @Override
    public List<Map<String, Object>> getStudentsWithoutGrade(Long courseId) {
        return gradeMapper.findStudentsWithoutGrade(courseId);
    }


    @Override
    public List<AlphabetSystem> findAlphabetBySubject(Long courseId) {
        // courseId로 subjectId를 찾아서 알파벳 규정 조회
        // 임시로 빈 리스트 반환 (과목별 규정이 없으므로)
        return Collections.emptyList();
    }


    @Override
    public Course getCourseById(Long courseId) {
        return gradeMapper.findCourseById(courseId);
    }

}
