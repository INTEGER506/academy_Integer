package com.ac.kr.academy.service.grade;


import com.ac.kr.academy.domain.course.Course;
import com.ac.kr.academy.domain.grade.AlphabetSystem;
import com.ac.kr.academy.domain.grade.Grade;
import com.ac.kr.academy.domain.grade.GradeSystem;
import com.ac.kr.academy.domain.subject.Subject;
import com.ac.kr.academy.dto.page.PageRequestDTO;
import com.ac.kr.academy.dto.page.PageResponseDTO;
import com.ac.kr.academy.mapper.grade.GradeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {

    private final GradeMapper gradeMapper;
    
    public GradeMapper getGradeMapper() { return gradeMapper; }
    
    // 학생 정보 조회 (임시 구현)
    @Override
    public Map<String, Object> getStudentInfoForGrade(Long studentId) {
        // 임시로 기본값 반환 (실제로는 UserService를 활용해야 함)
        Map<String, Object> studentInfo = new HashMap<>();
        studentInfo.put("studentId", studentId);
        studentInfo.put("studentName", "학생정보");
        studentInfo.put("studentNo", "20250001");
        studentInfo.put("email", "student@example.com");
        studentInfo.put("phone", "010-1234-5678");
        studentInfo.put("major", "컴퓨터공학과");
        studentInfo.put("admissionYear", "2025");
        return studentInfo;
    }


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
    // 성적 등록: 점수 계산 적용 후 저장, 상대평가 시 전체 재계산 트리거
    public void addGrade(Grade grade, Long professorId) {
        
        enforceProfessorOwnershipIfNeeded(grade.getId(), professorId, grade.getEnrollmentId());
        
        Long subjectId = null;
        if (grade.getEnrollmentId() != null) {
            subjectId = gradeMapper.findSubjectIdByEnrollment(grade.getEnrollmentId());
        }
        
        applyCalcWithRules(grade, subjectId, grade.getEnrollmentId());
        gradeMapper.insert(grade);
        
        try {
            Long courseId = gradeMapper.findCourseIdByEnrollment(grade.getEnrollmentId());
            if (courseId != null && isRelativeGrading(courseId)) {
                recalculateAllGradesForCourse(courseId);
            }
        } catch (Exception e) {
            log.error("실시간 상대평가 재계산 실패", e);
        }
    }

    // 성적 수정
    @Override
    @Transactional
    // 성적 수정: 기존 점수 갱신 및 상대평가 시 전체 재계산 트리거
    public void editGrade(Grade grade, Long professorId) {
        enforceProfessorOwnershipIfNeeded(grade.getId(), professorId, grade.getEnrollmentId());
        
        Long subjectId = null;
        if (grade.getEnrollmentId() != null) {
            subjectId = gradeMapper.findSubjectIdByEnrollment(grade.getEnrollmentId());
        }
        
        applyCalcWithRules(grade, subjectId, grade.getEnrollmentId());
        gradeMapper.update(grade);
        
        try {
            Long courseId = gradeMapper.findCourseIdByEnrollment(grade.getEnrollmentId());
            if (courseId != null && isRelativeGrading(courseId)) {
                recalculateAllGradesForCourse(courseId);
            }
        } catch (Exception e) {
            log.error("실시간 상대평가 재계산 실패", e);
        }
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

    // 교수가 개설한 강의 목록 조회 (임시 구현)
    @Override
    public PageResponseDTO<Map<String, Object>> listProfessorCoursesFromCourseService(Long professorId, 
                                                                                     String searchType, 
                                                                                     String searchKeyword, 
                                                                                     PageRequestDTO req) {
        // 임시로 기본 데이터 반환 (실제로는 CourseService를 활용해야 함)
        List<Map<String, Object>> rows = new ArrayList<>();
        
        // 샘플 데이터 생성
        Map<String, Object> sampleCourse = new HashMap<>();
        sampleCourse.put("courseId", 1L);
        sampleCourse.put("courseName", "자바프로그래밍");
        sampleCourse.put("subjectId", 1L);
        sampleCourse.put("subjectName", "자바프로그래밍");
        sampleCourse.put("credit", 3);
        sampleCourse.put("semesterId", 1L);
        sampleCourse.put("semesterName", "2025-1학기");
        sampleCourse.put("semesterYear", "2025-1학기");
        sampleCourse.put("enrollmentCount", 25);
        rows.add(sampleCourse);
        
        return PageResponseDTO.pageOf(rows, 1, req);
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
    
    public GradeSystem getGradeSystemById(Long id) { return gradeMapper.getGradeSystemById(id); }

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

    // 학생 수강신청 목록 조회 (성적 등록 여부 포함)
    @Override
    public PageResponseDTO<Map<String, Object>> listMyEnrollmentsWithGrades(Long studentId,
                                                                            String searchType,
                                                                            String searchKeyword,
                                                                            PageRequestDTO req) {
        
        long total = gradeMapper.countMyEnrollmentsWithGrades(studentId, searchType, searchKeyword);

        List<Map<String, Object>> list = (total == 0)
                ? Collections.emptyList()
                : gradeMapper.findMyEnrollmentsWithGrades(
                studentId, searchType, searchKeyword, req.getStart(), req.getEnd()
        );

        return PageResponseDTO.pageOf(list, total, req);
    }

    // 학기별 성적 조회 (1~8학기)
    @Override
    public PageResponseDTO<Map<String, Object>> listMyGradesBySemester(Long studentId) {
        List<Map<String, Object>> semesterGrades = gradeMapper.findMyGradesBySemester(studentId);
        
        // 8학기까지 빈 데이터로 채우기
        List<Map<String, Object>> result = new ArrayList<>();
        for (int semester = 1; semester <= 8; semester++) {
            final int currentSemester = semester; // final 변수로 복사
            Map<String, Object> semesterData = new HashMap<>();
            semesterData.put("semester", currentSemester);
            
            // 해당 학기 데이터 찾기
            Optional<Map<String, Object>> found = semesterGrades.stream()
                    .filter(data -> ((Number) data.get("SEMESTER")).intValue() == currentSemester)
                    .findFirst();
            
            if (found.isPresent()) {
                Map<String, Object> data = found.get();
                semesterData.put("appliedCredits", data.get("APPLIED_CREDITS"));
                semesterData.put("acquiredCredits", data.get("ACQUIRED_CREDITS"));
                semesterData.put("totalScore", data.get("TOTAL_SCORE"));
                semesterData.put("averageGpa", data.get("AVERAGE_GPA"));
                semesterData.put("hasData", true);
            } else {
                semesterData.put("appliedCredits", 0.0);
                semesterData.put("acquiredCredits", 0.0);
                semesterData.put("totalScore", 0.0);
                semesterData.put("averageGpa", 0.0);
                semesterData.put("hasData", false);
            }
            
            result.add(semesterData);
        }
        
        return PageResponseDTO.<Map<String, Object>>builder()
                .data(result)
                .totalCount(8)
                .currentPage(1)
                .pageSize(8)
                .totalPage(1)
                .build();
    }

    // 학기별 상세 성적 조회 (특정 학기의 과목별 성적)
    @Override
    public PageResponseDTO<Map<String, Object>> listMyGradesBySemesterDetail(Long studentId, Long semesterId) {
        List<Map<String, Object>> semesterDetailGrades = gradeMapper.findMyGradesBySemesterDetail(studentId, semesterId);
        
        return PageResponseDTO.<Map<String, Object>>builder()
                .data(semesterDetailGrades)
                .totalCount(semesterDetailGrades.size())
                .currentPage(1)
                .pageSize(semesterDetailGrades.size())
                .totalPage(1)
                .build();
    }

    // 성적 단건 조회
    @Override
    public Grade getMyGrade(Long studentId, Long id) {
        // 학생 단건 성적 조회 (학생 소유 검증은 쿼리 조건으로 처리)
        return (Grade) gradeMapper.findMyGradeById(id, studentId);
    }

    /*    =========================================================================================================================*/
    @Override
    public Grade findGrade(Long id) {
        return gradeMapper.findById(id);
    }


    @Override
    public void addAlphabetRule(AlphabetSystem rule) {
        // 규정 추가: 유효성 검사 후 과목별 규정으로 저장
        // courseId가 있으면 과목 전용 규정으로 저장,
        // 없으면 글로벌 규정으로 저장
        if (rule == null) return;
        validateAlphabetRule(rule);
        gradeMapper.insertAlphabetBySubject(rule);
    }

    private void validateAlphabetRule(AlphabetSystem rule) {
        // 허용 학점
        String a = rule.getAlphabet();
        if (a == null) throw new IllegalArgumentException("alphabet is required");
        switch (a) {
            case "A+": case "A": case "B+": case "B": case "C+": case "C": case "D": case "F":
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
    @Transactional
    // 글로벌 규정 저장: 기존 삭제 후 새 규정 일괄 저장 및 과목에 반영
    public void saveGlobalRules(Map<String, String> params) {
        
        // 파라미터 검증
        if (params == null || params.isEmpty()) {
            throw new IllegalArgumentException("저장할 규정 데이터가 없습니다.");
        }
        
        gradeMapper.deleteAlphabetGlobal();
        
        String[] grades = {"A+", "A", "B+", "B", "C+", "C", "D+", "D"};
        
        for (String grade : grades) {
            String boundaryKey = "boundary_" + grade;
            if (params.containsKey(boundaryKey)) {
                try {
                    double boundary = Double.parseDouble(params.get(boundaryKey));
                    if (boundary >= 0.0 && boundary <= 100.0) {
                        AlphabetSystem rule = new AlphabetSystem();
                        rule.setAlphabet(grade);
                        rule.setBoundary(boundary);
                        rule.setCourseId(null); // 글로벌 규정
                        rule.setDescription("상위 " + boundary + "%");
                        gradeMapper.insertAlphabetGlobal(rule);
                    } else {
                        log.warn("잘못된 비율 값: {} = {}% (0-100 범위를 벗어남)", grade, boundary);
                    }
                } catch (NumberFormatException e) {
                    log.warn("잘못된 숫자 형식: {} = {}", grade, params.get(boundaryKey));
                }
            }
        }
        
        
        try {
            List<Subject> allSubjects = getAllSubjects();
            if (allSubjects != null && !allSubjects.isEmpty()) {
                for (Subject subject : allSubjects) {
                    try {
                        resetSubjectToGlobal(subject.getId());
                    } catch (Exception e) {
                        log.error("과목 ID {}에 글로벌 규정 적용 실패", subject.getId(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("모든 과목에 글로벌 규정 적용 중 오류 발생", e);
        }
    }


    @Override
    @Transactional
    // 과목 규정 초기화: 해당 과목의 커스텀 규정 삭제
    public void resetSubjectToGlobal(Long subjectId) {
        
        gradeMapper.deleteAlphabetBySubject(subjectId);
    }

    @Override
    @Transactional
    // 과목 규정 인라인 저장: 입력값 검증 후 저장, 상대평가 강의 일괄 재계산
    public void saveSubjectRulesInline(Map<String, String> params) {
        
        String subjectIdStr = params.get("subjectId");
        if (subjectIdStr == null || subjectIdStr.trim().isEmpty()) {
            throw new IllegalArgumentException("subjectId가 비어있습니다.");
        }
        Long subjectId = Long.parseLong(subjectIdStr);
        gradeMapper.deleteAlphabetBySubject(subjectId);
        
        String[] grades = {"A+", "A", "B+", "B", "C+", "C", "D", "F"};
        for (String grade : grades) {
            String value = params.get("boundary_" + grade);
            if (value != null && !value.trim().isEmpty()) {
                try {
                    Double percentage = Double.parseDouble(value);
                    if (percentage >= 0.0 && percentage <= 100.0 && percentage == Math.floor(percentage * 10) / 10) {
                        AlphabetSystem rule = new AlphabetSystem();
                        rule.setCourseId(subjectId);
                        rule.setAlphabet(grade);
                        rule.setBoundary(percentage);
                        rule.setDescription("상위 " + percentage + "%");
                        gradeMapper.insertAlphabetBySubject(rule);
                    } else {
                        throw new IllegalArgumentException("유효하지 않은 비율 값: " + percentage + "% (학점: " + grade + ")");
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("잘못된 숫자 형식: " + value + " (학점: " + grade + ")");
                }
            }
        }
        
        try {
            List<Long> courseIds = gradeMapper.findCourseIdsBySubject(subjectId);
            if (courseIds != null && !courseIds.isEmpty()) {
                for (Long courseId : courseIds) {
                    try {
                        if (isRelativeGrading(courseId)) {
                            recalculateAllGradesForCourse(courseId);
                        }
                    } catch (Exception e) {
                        log.error("규정 변경으로 인한 재계산 실패 - courseId: {}", courseId, e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("규정 변경으로 인한 재계산 중 오류 발생", e);
        }
    }

    @Override
    @Transactional
    // 강의 전체 성적 재계산: 동일 규칙으로 성적 재산출 후 저장
    public void recalculateAllGradesForCourse(Long courseId) {
        if (courseId == null) return;
        
        List<Grade> allGrades = gradeMapper.findAllGradesByCourse(courseId);
        if (allGrades == null || allGrades.isEmpty()) return;
        
        Long subjectId = gradeMapper.findSubjectIdByCourse(courseId);
        
        for (Grade grade : allGrades) {
            try {
                applyCalcWithRules(grade, subjectId, grade.getEnrollmentId());
                gradeMapper.update(grade);
            } catch (Exception e) {
                log.error("성적 재계산 실패 - Grade ID: {}", grade.getId(), e);
            }
        }
    }
    
    private boolean isRelativeGrading(Long courseId) {
        // 상대평가 여부: 해당 강의의 성적 레코드 수 기준(5명 이상)
        try {
            List<Grade> allGrades = gradeMapper.findAllGradesByCourse(courseId);
            int studentCount = allGrades != null ? allGrades.size() : 0;
            return studentCount >= 5;
        } catch (Exception e) {
            log.error("강의 평가 방식 확인 실패 - courseId: {}", courseId, e);
            return false;
        }
    }

    @Override
    @Transactional
    // 글로벌 규정 복제: 글로벌 → 과목 규정으로 복사 생성
    public void createCustomRulesFromGlobal(Long subjectId) {
        List<AlphabetSystem> globalRules = gradeMapper.findGlobalAlphabetRules();
        for (AlphabetSystem globalRule : globalRules) {
            AlphabetSystem customRule = new AlphabetSystem();
            customRule.setCourseId(subjectId);
            customRule.setAlphabet(globalRule.getAlphabet());
            customRule.setBoundary(globalRule.getBoundary());
            customRule.setDescription(globalRule.getDescription());
            gradeMapper.insertAlphabetBySubject(customRule);
        }
    }

    @Override
    public List<Subject> getAllSubjects() {
        // 임시로 빈 리스트 반환 (실제로는 SubjectService를 활용해야 함)
        return new ArrayList<>();
    }

    /* ================= 계산용: 규칙 로딩 + 산출 ================= */

    // 규칙 로딩 후 총점/학점/GPA 계산 파이프라인
    private void applyCalcWithRules(Grade grade, Long subjectId, Long enrollmentId) {
        Long courseId = null;
        if (grade.getEnrollmentId() != null) {
            courseId = gradeMapper.findCourseIdByEnrollment(grade.getEnrollmentId());
        }

        if (grade.getEnrollmentId() != null && grade.getStudentId() == null) {
            Long studentId = gradeMapper.findStudentIdByEnrollment(grade.getEnrollmentId());
            grade.setStudentId(studentId);
        }

        GradeSystem gs = (courseId != null) ? gradeMapper.findGradeSystemByCourse(courseId) : null;

        List<AlphabetSystem> rulesAs = null;
        
        if (subjectId != null || enrollmentId != null) {
            rulesAs = gradeMapper.listAlphabetBySubjectAll(subjectId, enrollmentId);
        }
        
        if (rulesAs == null || rulesAs.isEmpty()) {
            rulesAs = gradeMapper.findGlobalAlphabetRules();
            
            if (rulesAs == null || rulesAs.isEmpty()) {
                throw new IllegalStateException("글로벌 규정이 설정되지 않았습니다. 관리자에게 문의하세요.");
            }
        }

        recalcWithRules(grade, gs, rulesAs, enrollmentId);
    }

    // null 안전 정수 변환
    private int i(Integer v) { return v == null ? 0 : v; }

    @Override
    // 기본 절대평가 규정 제공(점수 기준)
    public List<AlphabetSystem> getDefaultAlphabetRules() {
        List<AlphabetSystem> defaultRules = new ArrayList<>();
        String[] grades = {"A+", "A", "B+", "B", "C+", "C", "D"};
        double[] boundaries = {95.0, 90.0, 85.0, 80.0, 75.0, 70.0, 60.0};
        
        for (int i = 0; i < grades.length; i++) {
            AlphabetSystem rule = new AlphabetSystem();
            rule.setAlphabet(grades[i]);
            rule.setBoundary(boundaries[i]);
            rule.setCourseId(null);
            rule.setDescription("절대평가 " + boundaries[i] + "점 이상");
            defaultRules.add(rule);
        }
        
        return defaultRules;
    }


    // 점수비율 적용 → 총점 산출 → 상대/절대 규정에 따른 학점/GPA 계산
    private void recalcWithRules(Grade grade, GradeSystem gs, List<AlphabetSystem> rulesAs, Long enrollmentId) {
        int midR = (gs != null && gs.getMidExamRatio()    != null) ? (int) Math.round(gs.getMidExamRatio())    : 30;
        int finR = (gs != null && gs.getFinalExamRatio()  != null) ? (int) Math.round(gs.getFinalExamRatio())  : 40;
        int asgR = (gs != null && gs.getAssignmentRatio() != null) ? (int) Math.round(gs.getAssignmentRatio()) : 20;
        int attR = (gs != null && gs.getAttendanceRatio() != null) ? (int) Math.round(gs.getAttendanceRatio()) : 10;


        double totalScore = (i(grade.getMidExam()) * midR / 100.0)
                          + (i(grade.getFinalExam()) * finR / 100.0)
                          + (i(grade.getAssignment()) * asgR / 100.0)
                          + (i(grade.getAttendance()) * attR / 100.0);

        int totalInt = (int) Math.round(totalScore);
        totalInt = Math.max(0, Math.min(totalInt, 100));
        
        
        grade.setTotalInt(totalInt);
        grade.setScore((long) totalInt);

        String alphabet;
        if (totalInt <= 30) {
            alphabet = "F";
        } else {
            alphabet = calculateAlphabetByPercentage(totalInt, rulesAs, grade.getEnrollmentId());
        }
        
        grade.setAlphabet(alphabet);
        Long gpaValue = mapAlphabetToGpa10(alphabet);
        gpaValue = Math.min(gpaValue, 45L);
        grade.setGpa(gpaValue);
    }

    // 상대/절대 평가에 따른 알파벳 학점 산출 (F는 총점<30에서만)
    private String calculateAlphabetByPercentage(int totalScore, List<AlphabetSystem> rules, Long enrollmentId) {
        
        if (rules == null || rules.isEmpty()) {
            return "F";
        }
        
        Long totalStudents = gradeMapper.countStudentsByEnrollment(enrollmentId);
        if (totalStudents == null || totalStudents <= 0) {
            return "F";
        }
        
        if (totalStudents < 5) {
            return calculateAbsoluteGrade(totalScore);
        }
        
        Long higherScoreCount = gradeMapper.countStudentsWithHigherScore(enrollmentId, totalScore);
        if (higherScoreCount == null) {
            higherScoreCount = 0L;
        }
        
        double percentile = ((double) higherScoreCount / totalStudents) * 100.0;
        percentile = Math.max(0.0, Math.min(100.0, percentile));
        
        List<AlphabetSystem> relativeRules = rules;
        
        List<AlphabetSystem> filteredRules = new ArrayList<>();
        for (AlphabetSystem r : relativeRules) {
            if (r != null && r.getAlphabet() != null && !"F".equals(r.getAlphabet())) {
                filteredRules.add(r);
            }
        }

        List<AlphabetSystem> sortedRules = new ArrayList<>(filteredRules);
        sortedRules.sort((a, b) -> {
            Double percentageA = a.getBoundary();
            Double percentageB = b.getBoundary();
            if (percentageA == null) percentageA = 0.0;
            if (percentageB == null) percentageB = 0.0;
            return Double.compare(percentageA, percentageB);
        });
        
        for (AlphabetSystem rule : sortedRules) {
            Double percentage = rule.getBoundary();
            if (percentage == null) continue;
            
            if (percentile <= percentage) {
                return rule.getAlphabet();
            }
        }
        
        String fallback = sortedRules.isEmpty() ? "D" : sortedRules.get(sortedRules.size() - 1).getAlphabet();
        return fallback;
    }
    
    // 절대평가: 점수 구간별 학점 매핑 (F는 30 미만)
    private String calculateAbsoluteGrade(int totalScore) {
        if (totalScore < 30) return "F";
        if (totalScore >= 95) return "A+";
        else if (totalScore >= 90) return "A";
        else if (totalScore >= 85) return "B+";
        else if (totalScore >= 80) return "B";
        else if (totalScore >= 75) return "C+";
        else if (totalScore >= 70) return "C";
        else return "D";
    }

    // 학점문자 → GPA×10 매핑 (A+=45 ... F=0)
    private long mapAlphabetToGpa10(String a) {
        if (a == null) return 0L;
        switch (a) {
            case "A+": return 45L;
            case "A":  return 40L;
            case "B+": return 35L;
            case "B":  return 30L;
            case "C+": return 25L;
            case "C":  return 20L;
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
        Course course = gradeMapper.findCourseById(courseId);
        if (course == null) {
            return Collections.emptyList();
        }
        
        Long subjectId = course.getSubjectId();
        List<AlphabetSystem> customRules = gradeMapper.listAlphabetBySubjectAll(subjectId, null);
        
        if (customRules != null && !customRules.isEmpty()) {
            customRules.sort((r1, r2) -> {
                String[] order = {"A+", "A", "B+", "B", "C+", "C", "D", "F"};
                int index1 = java.util.Arrays.asList(order).indexOf(r1.getAlphabet());
                int index2 = java.util.Arrays.asList(order).indexOf(r2.getAlphabet());
                return Integer.compare(index1, index2);
            });
            
            return customRules;
        } else {
            return Collections.emptyList();
        }
    }


    @Override
    public Course getCourseById(Long courseId) {
        return gradeMapper.findCourseById(courseId);
    }

    @Override
    public PageResponseDTO<Map<String, Object>> listSubjectRulesStatus(String searchType, 
                                                                       String searchKeyword, 
                                                                       PageRequestDTO req) {
        long total = gradeMapper.countSubjectRules(searchType, searchKeyword);
        int start = (req.getPage() - 1) * req.getPageSize() + 1;
        int end = req.getPage() * req.getPageSize();
        
        List<Map<String, Object>> data = gradeMapper.findSubjectRulesStatus(searchType, searchKeyword, start, end);
        
        List<AlphabetSystem> globalRules = getDefaultAlphabetRules();
        
        for (Map<String, Object> subject : data) {
            Object subjectIdObj = subject.get("subjectId");
            Long subjectId = null;
            if (subjectIdObj instanceof java.math.BigDecimal) {
                subjectId = ((java.math.BigDecimal) subjectIdObj).longValue();
            } else if (subjectIdObj instanceof Number) {
                subjectId = ((Number) subjectIdObj).longValue();
            }
            
            List<AlphabetSystem> customRules = null;
            if (subjectId != null) {
                customRules = gradeMapper.listAlphabetBySubjectAll(subjectId, null);
            }
            

            if (customRules != null && !customRules.isEmpty()) {
                boolean isDifferentFromGlobal = false;
                Map<String, Double> globalRuleMap = new HashMap<>();
                
                for (AlphabetSystem globalRule : globalRules) {
                    globalRuleMap.put(globalRule.getAlphabet(), globalRule.getBoundary());
                }
                
                for (AlphabetSystem customRule : customRules) {
                    Double globalValue = globalRuleMap.get(customRule.getAlphabet());
                    if (globalValue != null && !globalValue.equals(customRule.getBoundary())) {
                        isDifferentFromGlobal = true;
                        break;
                    }
                }
                
                if (isDifferentFromGlobal) {
                    subject.put("ruleType", "CUSTOM");
                    subject.put("rules", customRules);
                } else {
                    subject.put("ruleType", "GLOBAL");
                    subject.put("rules", globalRules);
                }
            } else {
                subject.put("ruleType", "GLOBAL");
                subject.put("rules", globalRules);
            }
        }
        
        return PageResponseDTO.pageOf(data, total, req);
    }


}
