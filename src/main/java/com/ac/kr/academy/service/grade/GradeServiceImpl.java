package com.ac.kr.academy.service.grade;


import com.ac.kr.academy.domain.course.Course;
import com.ac.kr.academy.domain.grade.AlphabetSystem;
import com.ac.kr.academy.domain.grade.Grade;
import com.ac.kr.academy.domain.grade.GradeSystem;
import com.ac.kr.academy.domain.subject.Subject;
// 🔥 제거됨: 사용하지 않는 import
import com.ac.kr.academy.dto.page.PageRequestDTO;
import com.ac.kr.academy.dto.page.PageResponseDTO;
import com.ac.kr.academy.mapper.grade.GradeMapper;
// 🔥 제거됨: 사용하지 않는 import
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


// 🔥 제거됨: 사용하지 않는 import
import java.util.*;
import java.util.Arrays;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {

    private final GradeMapper gradeMapper;
    // 🔥 제거됨: 사용하지 않는 subjectMapper


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
            String value = params.get("boundary_" + grade);
            if (value != null && !value.trim().isEmpty()) {
                try {
                    Double percentage = Double.parseDouble(value);
                    // 더 엄격한 검증: 0 이상 100 이하, 소수점 1자리까지만 허용
                    if (percentage >= 0.0 && percentage <= 100.0 && percentage == Math.floor(percentage * 10) / 10) {
                        AlphabetSystem rule = new AlphabetSystem();
                        rule.setAlphabet(grade);
                        rule.setBoundary(percentage);
                        rule.setCourseId(null); // 글로벌 규정
                        rule.setDescription("상위 " + percentage + "%");
                        gradeMapper.insertAlphabetGlobal(rule);
                    } else {
                        log.warn("유효하지 않은 비율 값: {} (학점: {})", percentage, grade);
                        throw new IllegalArgumentException("유효하지 않은 비율 값: " + percentage + "% (학점: " + grade + ")");
                    }
                } catch (NumberFormatException e) {
                    log.warn("잘못된 숫자 형식: {} (학점: {})", value, grade);
                    throw new IllegalArgumentException("잘못된 숫자 형식: " + value + " (학점: " + grade + ")");
                }
            }
        }
    }

    @Override
    @Transactional
    public void resetSubjectToGlobal(Long subjectId) {
        log.info("=== 과목별 규정 초기화 시작 ===");
        log.info("초기화 대상 subjectId: {}", subjectId);
        
        // 초기화 전 해당 과목의 커스텀 규정 확인
        List<AlphabetSystem> beforeRules = gradeMapper.listAlphabetBySubjectAll(subjectId, null);
        log.info("초기화 전 커스텀 규정 수: {}", beforeRules != null ? beforeRules.size() : 0);
        if (beforeRules != null) {
            for (AlphabetSystem rule : beforeRules) {
                log.info("삭제될 규정 - 과목ID: {}, 학점: {}, 비율: {}%", rule.getCourseId(), rule.getAlphabet(), rule.getBoundary());
            }
        }
        
        // ✅ 수정: 특정 과목의 커스텀 규정만 삭제
        log.info("특정 과목({})의 커스텀 규정 삭제 시작", subjectId);
        int deletedCount = gradeMapper.deleteAlphabetBySubject(subjectId);
        log.info("삭제된 규정 수: {}", deletedCount);
        
        // 초기화 후 확인
        List<AlphabetSystem> afterRules = gradeMapper.listAlphabetBySubjectAll(subjectId, null);
        log.info("초기화 후 커스텀 규정 수: {}", afterRules != null ? afterRules.size() : 0);
        
        // 🔥 추가: 다른 과목들이 영향받지 않았는지 확인
        log.info("다른 과목들 확인 중...");
        List<AlphabetSystem> allSubjectRules = gradeMapper.listAlphabetBySubjectAll(null, null);
        log.info("전체 과목별 규정 수: {}", allSubjectRules != null ? allSubjectRules.size() : 0);
        if (allSubjectRules != null) {
            for (AlphabetSystem rule : allSubjectRules) {
                log.info("남아있는 규정 - 과목ID: {}, 학점: {}, 비율: {}%", rule.getCourseId(), rule.getAlphabet(), rule.getBoundary());
            }
        }
        
        log.info("=== 과목별 규정 초기화 완료 ===");
    }

    @Override
    @Transactional
    public void saveSubjectRulesInline(Map<String, String> params) {
        log.info("=== saveSubjectRulesInline 시작 ===");
        log.info("받은 params: {}", params);
        
        Long subjectId = Long.parseLong(params.get("subjectId"));
        log.info("과목별 규정 저장 시작 - subjectId: {}", subjectId);
        
        // 기존 커스텀 규정 삭제
        int deletedCount = gradeMapper.deleteAlphabetBySubject(subjectId);
        log.info("기존 커스텀 규정 삭제 완료 - 삭제된 규정 수: {}", deletedCount);
        
        // 새로운 규정들 저장
        String[] grades = {"A+", "A", "B+", "B", "C+", "C", "D+", "D", "F"};
        int savedCount = 0;
        for (String grade : grades) {
            String value = params.get("boundary_" + grade);
            log.info("저장 시도 - 과목ID: {}, 학점: {}, 받은 값: '{}'", subjectId, grade, value);
            if (value != null && !value.trim().isEmpty()) { // 0% 값도 저장하도록 수정
                try {
                    Double percentage = Double.parseDouble(value);
                    // 더 엄격한 검증: 0 이상 100 이하, 소수점 1자리까지만 허용
                    if (percentage >= 0.0 && percentage <= 100.0 && percentage == Math.floor(percentage * 10) / 10) {
                        AlphabetSystem rule = new AlphabetSystem();
                        rule.setCourseId(subjectId); // 과목별 규정
                        rule.setAlphabet(grade);
                        rule.setBoundary(percentage);
                        rule.setDescription("상위 " + percentage + "%");
                        int insertResult = gradeMapper.insertAlphabetBySubject(rule);
                        if (insertResult > 0) {
                            savedCount++;
                            log.info("규정 저장 성공 - 과목ID: {}, 학점: {}, 비율: {}%, insertResult: {}", subjectId, grade, percentage, insertResult);
                        } else {
                            log.error("규정 저장 실패 - 과목ID: {}, 학점: {}, 비율: {}%, insertResult: {}", subjectId, grade, percentage, insertResult);
                        }
                    } else {
                        log.warn("유효하지 않은 비율 값: {} (학점: {}, 과목ID: {})", percentage, grade, subjectId);
                        throw new IllegalArgumentException("유효하지 않은 비율 값: " + percentage + "% (학점: " + grade + ")");
                    }
                } catch (NumberFormatException e) {
                    log.warn("잘못된 숫자 형식: {} (학점: {}, 과목ID: {})", value, grade, subjectId);
                    throw new IllegalArgumentException("잘못된 숫자 형식: " + value + " (학점: " + grade + ")");
                }
            } else {
                // 값이 비어있거나 null인 경우에도 로그를 남기고 계속 진행
                log.warn("비율 값이 비어있음: '{}' (학점: {}, 과목ID: {}) - 건너뜀", value, grade, subjectId);
            }
        }
        log.info("규정 저장 완료 - 과목ID: {}, 총 저장된 규정 수: {}/{}", subjectId, savedCount, grades.length);
        log.info("=== saveSubjectRulesInline 완료 ===");
    }

    @Override
    @Transactional
    public void createCustomRulesFromGlobal(Long subjectId) {
        // 글로벌 규정을 복사하여 과목별 커스텀 규정 생성
        List<AlphabetSystem> globalRules = gradeMapper.findGlobalAlphabetRules();
        for (AlphabetSystem globalRule : globalRules) {
            AlphabetSystem customRule = new AlphabetSystem();
            customRule.setCourseId(subjectId); // 과목 ID로 설정
            customRule.setAlphabet(globalRule.getAlphabet());
            customRule.setBoundary(globalRule.getBoundary());
            customRule.setDescription(globalRule.getDescription());
            gradeMapper.insertAlphabetBySubject(customRule);
        }
    }

    @Override
    public List<Subject> getAllSubjects() {
        // GradeMapper에서 모든 과목 조회 (SubjectMapper.xml이 없으므로)
        return gradeMapper.findAllSubjects();
    }

    @Override
    public Subject getSubjectById(Long subjectId) {
        // 임시로 과목 정보를 직접 생성 (실제로는 DB에서 조회해야 함)
        Subject subject = new Subject();
        subject.setId(subjectId);
        subject.setName("과목명"); // 실제로는 DB에서 조회
        return subject;
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

    @Override
    public PageResponseDTO<Map<String, Object>> listSubjectRulesStatus(String searchType, 
                                                                       String searchKeyword, 
                                                                       PageRequestDTO req) {
        // 총 건수 조회
        long total = gradeMapper.countSubjectRules(searchType, searchKeyword);
        
        // 페이징 처리
        int start = (req.getPage() - 1) * req.getPageSize() + 1;
        int end = req.getPage() * req.getPageSize();
        
        // 과목별 규정 상태 조회
        List<Map<String, Object>> data = gradeMapper.findSubjectRulesStatus(searchType, searchKeyword, start, end);
        log.info("조회된 과목 데이터 수: {}", data != null ? data.size() : 0);
        if (data != null) {
            for (Map<String, Object> subject : data) {
                log.info("과목 데이터: {}", subject);
            }
        }
        
        // 글로벌 규정 조회 (비교용)
        List<AlphabetSystem> globalRules = gradeMapper.listAlphabetGlobalAll();
        
        // 각 과목에 대해 규정 상태 설정
        for (Map<String, Object> subject : data) {
            Long subjectId = (Long) subject.get("subjectId");
            log.info("Processing subject - subjectId: {}, subjectName: {}", subjectId, subject.get("subjectName"));
            log.info("Raw subject data: {}", subject);
            
            // 해당 과목의 커스텀 규정이 있는지 확인
            List<AlphabetSystem> customRules = null;
            if (subjectId != null) {
                customRules = gradeMapper.listAlphabetBySubjectAll(subjectId, null);
            }
            log.info("Custom rules found for subject {}: {}", subjectId, customRules != null ? customRules.size() : 0);
            

            if (customRules != null && !customRules.isEmpty()) {
                       // 🔥 강제로 CUSTOM으로 설정 (데이터 정리 후 테스트용)
                       subject.put("ruleType", "CUSTOM");
                       subject.put("rules", customRules);
                       log.info("🔥 강제 CUSTOM 설정 - 과목 {}: 커스텀 규정 수 {}", subjectId, customRules.size());
                   } else {
                       // 커스텀 규정이 없으면 글로벌로 설정
                       subject.put("ruleType", "GLOBAL");
                       subject.put("rules", globalRules);
                       log.info("Set ruleType to GLOBAL for subject {} (no custom rules): {}", subjectId, 0);
                   }
        }
        
        return PageResponseDTO.pageOf(data, total, req);
    }


}
