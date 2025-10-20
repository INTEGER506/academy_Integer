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

@Slf4j
@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {

    private final GradeMapper gradeMapper;
    
    public GradeMapper getGradeMapper() {
        return gradeMapper;
    }
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
        log.info("=== 성적 등록 시작 ===");
        log.info("입력된 성적: enrollmentId={}, midExam={}, finalExam={}, assignment={}, attendance={}", 
                grade.getEnrollmentId(), grade.getMidExam(), grade.getFinalExam(), 
                grade.getAssignment(), grade.getAttendance());
        
        enforceProfessorOwnershipIfNeeded(grade.getId(), professorId, grade.getEnrollmentId());
        
        // 🔥 수정: enrollmentId로부터 subjectId 조회하여 과목별 커스텀 규정 적용
        Long subjectId = null;
        if (grade.getEnrollmentId() != null) {
            subjectId = gradeMapper.findSubjectIdByEnrollment(grade.getEnrollmentId());
            log.info("조회된 subjectId: {}", subjectId);
        }
        
        applyCalcWithRules(grade, subjectId, grade.getEnrollmentId());
        
        log.info("계산된 성적: totalInt={}, alphabet={}, gpa={}", 
                grade.getTotalInt(), grade.getAlphabet(), grade.getGpa());
        
        gradeMapper.insert(grade);
        log.info("성적 등록 완료");
        
        // 🔥 실시간 상대평가: 성적 등록 후 전체 재계산 (상대평가만)
        try {
            Long courseId = gradeMapper.findCourseIdByEnrollment(grade.getEnrollmentId());
            if (courseId != null) {
                // 상대평가인 경우에만 재계산 (성능 최적화)
                if (isRelativeGrading(courseId)) {
                    log.info("실시간 상대평가 재계산 시작 - courseId: {}", courseId);
                    recalculateAllGradesForCourse(courseId);
                    log.info("실시간 상대평가 재계산 완료");
                } else {
                    log.info("절대평가 강의이므로 재계산 생략 - courseId: {}", courseId);
                }
            }
        } catch (Exception e) {
            log.error("실시간 상대평가 재계산 실패", e);
            // 실시간 재계산 실패해도 성적 등록은 성공으로 처리
        }
    }

    // 성적 수정
    @Override
    @Transactional
    public void editGrade(Grade grade, Long professorId) {
        log.info("=== 성적 수정 시작 ===");
        log.info("수정할 성적: ID={}, enrollmentId={}, midExam={}, finalExam={}, assignment={}, attendance={}", 
                grade.getId(), grade.getEnrollmentId(), grade.getMidExam(), grade.getFinalExam(), 
                grade.getAssignment(), grade.getAttendance());
        
        enforceProfessorOwnershipIfNeeded(grade.getId(), professorId, grade.getEnrollmentId());
        
        // 🔥 수정: enrollmentId로부터 subjectId 조회하여 과목별 커스텀 규정 적용
        Long subjectId = null;
        if (grade.getEnrollmentId() != null) {
            subjectId = gradeMapper.findSubjectIdByEnrollment(grade.getEnrollmentId());
            log.info("조회된 subjectId: {}", subjectId);
        }
        
        applyCalcWithRules(grade, subjectId, grade.getEnrollmentId());
        
        log.info("계산된 성적: totalInt={}, alphabet={}, gpa={}", 
                grade.getTotalInt(), grade.getAlphabet(), grade.getGpa());

        gradeMapper.update(grade);
        log.info("성적 수정 완료 - ID: {}", grade.getId());
        
        // 🔥 실시간 상대평가: 성적 수정 후 전체 재계산 (상대평가만)
        try {
            Long courseId = gradeMapper.findCourseIdByEnrollment(grade.getEnrollmentId());
            if (courseId != null) {
                // 상대평가인 경우에만 재계산 (성능 최적화)
                if (isRelativeGrading(courseId)) {
                    log.info("실시간 상대평가 재계산 시작 - courseId: {}", courseId);
                    recalculateAllGradesForCourse(courseId);
                    log.info("실시간 상대평가 재계산 완료");
                } else {
                    log.info("절대평가 강의이므로 재계산 생략 - courseId: {}", courseId);
                }
            }
        } catch (Exception e) {
            log.error("실시간 상대평가 재계산 실패", e);
            // 실시간 재계산 실패해도 성적 수정은 성공으로 처리
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
        log.info("=== 학기별 상세 성적 조회 시작 ===");
        log.info("studentId: {}, semesterId: {}", studentId, semesterId);
        
        List<Map<String, Object>> semesterDetailGrades = gradeMapper.findMyGradesBySemesterDetail(studentId, semesterId);
        log.info("조회된 성적 데이터 수: {}", semesterDetailGrades != null ? semesterDetailGrades.size() : 0);
        
        if (semesterDetailGrades != null && !semesterDetailGrades.isEmpty()) {
            log.info("=== 조회된 성적 데이터 상세 ===");
            for (int i = 0; i < semesterDetailGrades.size(); i++) {
                Map<String, Object> subject = semesterDetailGrades.get(i);
                log.info("과목 {}: {}", i + 1, subject);
                log.info("  - subjectName: {}", subject.get("subjectName"));
                log.info("  - alphabet: {}", subject.get("alphabet"));
                log.info("  - gpa: {}", subject.get("gpa"));
                log.info("  - credit: {}", subject.get("credit"));
            }
            log.info("=== 조회된 성적 데이터 상세 끝 ===");
        } else {
            log.warn("성적 데이터가 없습니다!");
        }
        
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
    public void saveGlobalRules(Map<String, String> params) {
        log.info("=== 글로벌 규정 저장 시작 ===");
        log.info("받은 params: {}", params);
        
        // 파라미터 검증
        if (params == null || params.isEmpty()) {
            throw new IllegalArgumentException("저장할 규정 데이터가 없습니다.");
        }
        
        // 글로벌 규정 삭제 후 재생성
        gradeMapper.deleteAlphabetGlobal();
        log.info("기존 글로벌 규정 삭제 완료");
        
        // 새로운 규정 저장
        String[] grades = {"A+", "A", "B+", "B", "C+", "C", "D+", "D"};
        int savedCount = 0;
        
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
                        savedCount++;
                        log.info("글로벌 규정 저장: {} = {}%", grade, boundary);
                    } else {
                        log.warn("잘못된 비율 값: {} = {}% (0-100 범위를 벗어남)", grade, boundary);
                    }
                } catch (NumberFormatException e) {
                    log.warn("잘못된 숫자 형식: {} = {}", grade, params.get(boundaryKey));
                }
            }
        }
        
        log.info("글로벌 규정 저장 완료 - 총 저장된 규정 수: {}/{}", savedCount, grades.length);
        
        // 글로벌 규정 수정 후 모든 과목의 규정을 자동으로 업데이트 (기존 메서드 활용)
        try {
            List<Subject> allSubjects = getAllSubjects();
            log.info("총 과목 수: {}", allSubjects != null ? allSubjects.size() : 0);
            
            if (allSubjects != null && !allSubjects.isEmpty()) {
                for (Subject subject : allSubjects) {
                    try {
                        resetSubjectToGlobal(subject.getId());
                        log.info("과목 ID {}에 글로벌 규정 적용 완료", subject.getId());
                    } catch (Exception e) {
                        log.error("과목 ID {}에 글로벌 규정 적용 실패", subject.getId(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("모든 과목에 글로벌 규정 적용 중 오류 발생", e);
        }
        
        log.info("=== 글로벌 규정 저장 완료 ===");
    }


    @Override
    @Transactional
    public void resetSubjectToGlobal(Long subjectId) {
        log.info("=== 과목별 규정 초기화 시작 ===");
        log.info("초기화 대상 subjectId: {}", subjectId);
        
        // 초기화 전 해당 과목의 커스텀 규정 확인
        List<AlphabetSystem> beforeRules = gradeMapper.listAlphabetBySubjectAll(subjectId, null);
        log.info("초기화 전 커스텀 규정 수: {}", beforeRules != null ? beforeRules.size() : 0);
        if (beforeRules != null && !beforeRules.isEmpty()) {
            log.info("초기화 전 커스텀 규정 상세:");
            for (AlphabetSystem rule : beforeRules) {
                log.info("  - 학점: {}, 비율: {}%", rule.getAlphabet(), rule.getBoundary());
            }
        }
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
        if (afterRules != null && !afterRules.isEmpty()) {
            log.error("⚠️ 초기화 후에도 커스텀 규정이 남아있음!");
            for (AlphabetSystem rule : afterRules) {
                log.error("  - 남아있는 규정: 학점: {}, 비율: {}%", rule.getAlphabet(), rule.getBoundary());
            }
        } else {
            log.info("✅ 초기화 성공: 커스텀 규정이 모두 삭제됨");
        }
        
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
        
        String subjectIdStr = params.get("subjectId");
        if (subjectIdStr == null || subjectIdStr.trim().isEmpty()) {
            throw new IllegalArgumentException("subjectId가 비어있습니다.");
        }
        Long subjectId = Long.parseLong(subjectIdStr);
        log.info("과목별 규정 저장 시작 - subjectId: {}", subjectId);
        
        // 기존 커스텀 규정 삭제
        int deletedCount = gradeMapper.deleteAlphabetBySubject(subjectId);
        log.info("기존 커스텀 규정 삭제 완료 - 삭제된 규정 수: {}", deletedCount);
        
        // 새로운 규정들 저장
        String[] grades = {"A+", "A", "B+", "B", "C+", "C", "D", "F"};
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
    public void recalculateAllGradesForCourse(Long courseId) {
        log.info("=== 전체 학생 학점 재계산 시작 ===");
        log.info("courseId: {}", courseId);
        
        if (courseId == null) {
            log.warn("courseId가 null입니다.");
            return;
        }
        
        // 해당 강의의 모든 성적 조회
        List<Grade> allGrades = gradeMapper.findAllGradesByCourse(courseId);
        log.info("재계산 대상 성적 수: {}", allGrades != null ? allGrades.size() : 0);
        
        if (allGrades == null || allGrades.isEmpty()) {
            log.warn("재계산할 성적이 없습니다.");
            return;
        }
        
        // subjectId 조회 (과목별 커스텀 규정 확인용)
        Long subjectId = gradeMapper.findSubjectIdByCourse(courseId);
        log.info("조회된 subjectId: {}", subjectId);
        
        // 각 성적에 대해 재계산
        int successCount = 0;
        int failCount = 0;
        
        for (Grade grade : allGrades) {
            try {
                log.info("성적 재계산 시작 - Grade ID: {}, enrollmentId: {}", grade.getId(), grade.getEnrollmentId());
                
                // 기존 점수로 재계산
                applyCalcWithRules(grade, subjectId, grade.getEnrollmentId());
                
                // DB 업데이트
                gradeMapper.update(grade);
                successCount++;
                
                log.info("성적 재계산 완료 - Grade ID: {}, 총점: {}, 학점: {}", 
                        grade.getId(), grade.getTotalInt(), grade.getAlphabet());
            } catch (Exception e) {
                failCount++;
                log.error("성적 재계산 실패 - Grade ID: {}, error: {}", grade.getId(), e.getMessage());
            }
        }
        
        log.info("=== 전체 학생 학점 재계산 완료 ===");
        log.info("성공: {}개, 실패: {}개", successCount, failCount);
    }
    
    /**
     * 강의가 상대평가인지 확인 (5명 이상이면 상대평가)
     */
    private boolean isRelativeGrading(Long courseId) {
        try {
            // 강의의 학생 수 조회
            List<Grade> allGrades = gradeMapper.findAllGradesByCourse(courseId);
            int studentCount = allGrades != null ? allGrades.size() : 0;
            
            boolean isRelative = studentCount >= 5;
            log.info("강의 평가 방식 확인 - courseId: {}, 학생수: {}, 상대평가: {}", 
                    courseId, studentCount, isRelative);
            
            return isRelative;
        } catch (Exception e) {
            log.error("강의 평가 방식 확인 실패 - courseId: {}", courseId, e);
            return false; // 오류 시 절대평가로 처리
        }
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

    /* ================= 계산용: 규칙 로딩 + 산출 ================= */

    private void applyCalcWithRules(Grade grade, Long subjectId, Long enrollmentId) {
        // 1) courseId 확보 (enrollmentId 기준)
        Long courseId = null;
        if (grade.getEnrollmentId() != null) {
            courseId = gradeMapper.findCourseIdByEnrollment(grade.getEnrollmentId());
        }

        // 2) studentId 설정 (enrollmentId 기준)5
        if (grade.getEnrollmentId() != null && grade.getStudentId() == null) {
            Long studentId = gradeMapper.findStudentIdByEnrollment(grade.getEnrollmentId());
            grade.setStudentId(studentId);
        }

        // 3) 코스별 점수비율 1건 (없으면 null → 기본 25% 사용)
        GradeSystem gs = (courseId != null) ? gradeMapper.findGradeSystemByCourse(courseId) : null;

        // 4) 등급 규정(as): 과목별 커스텀 → 글로벌 → 기본 규정 순으로 적용
        List<AlphabetSystem> rulesAs = null;
        
        // 1단계: 과목별 커스텀 규정 확인
        if (subjectId != null || enrollmentId != null) {
            rulesAs = gradeMapper.listAlphabetBySubjectAll(subjectId, enrollmentId);
            log.info("과목별 커스텀 규정 조회 결과: {}개", rulesAs != null ? rulesAs.size() : 0);
        }
        
        // 2단계: 커스텀 규정이 없으면 글로벌 규정 조회 (글로벌 규정은 항상 있어야 함)
        if (rulesAs == null || rulesAs.isEmpty()) {
            rulesAs = gradeMapper.findGlobalAlphabetRules();
            log.info("글로벌 규정 조회 결과: {}개", rulesAs != null ? rulesAs.size() : 0);
            
            // 글로벌 규정이 없으면 에러 발생 (글로벌 규정은 필수)
            if (rulesAs == null || rulesAs.isEmpty()) {
                log.error("글로벌 규정이 없습니다! 관리자가 글로벌 규정을 설정해야 합니다.");
                throw new IllegalStateException("글로벌 규정이 설정되지 않았습니다. 관리자에게 문의하세요.");
            }
        }

        // 5) 실제 계산
        recalcWithRules(grade, gs, rulesAs, enrollmentId);

    }

    private int i(Integer v) { return v == null ? 0 : v; }

    // 기본 알파벳 규정 반환 (사용자 지정 기준)
    @Override
    public List<AlphabetSystem> getDefaultAlphabetRules() {
        List<AlphabetSystem> defaultRules = new ArrayList<>();
        // 절대평가용 규정 (점수 기준)
        String[] grades = {"A+", "A", "B+", "B", "C+", "C", "D"};
        double[] boundaries = {95.0, 90.0, 85.0, 80.0, 75.0, 70.0, 60.0}; // 점수 기준으로 변경
        
        for (int i = 0; i < grades.length; i++) {
            AlphabetSystem rule = new AlphabetSystem();
            rule.setAlphabet(grades[i]);
            rule.setBoundary(boundaries[i]);
            rule.setCourseId(null); // 글로벌 규정
            rule.setDescription("절대평가 " + boundaries[i] + "점 이상");
            defaultRules.add(rule);
        }
        
        return defaultRules;
    }

    // 기본 상대평가 규정 (관리자 규정이 없을 때 사용)
    private List<AlphabetSystem> getDefaultRelativeRules() {
        List<AlphabetSystem> relativeRules = new ArrayList<>();
        String[] grades = {"A+", "A", "B+", "B", "C+", "C", "D"};
        double[] boundaries = {10.0, 20.0, 35.0, 50.0, 65.0, 80.0, 95.0}; // 더 엄격한 상위 퍼센트 기준
        
        for (int i = 0; i < grades.length; i++) {
            AlphabetSystem rule = new AlphabetSystem();
            rule.setAlphabet(grades[i]);
            rule.setBoundary(boundaries[i]);
            rule.setCourseId(null); // 글로벌 규정
            rule.setDescription("상위 " + boundaries[i] + "%");
            relativeRules.add(rule);
        }
        
        return relativeRules;
    }

    private void recalcWithRules(Grade grade, GradeSystem gs, List<AlphabetSystem> rulesAs, Long enrollmentId) {
        // 기본 비율: 중간 30%, 기말 40%, 과제 20%, 출석 10%
        int midR = (gs != null && gs.getMidExamRatio()    != null) ? (int) Math.round(gs.getMidExamRatio())    : 30;
        int finR = (gs != null && gs.getFinalExamRatio()  != null) ? (int) Math.round(gs.getFinalExamRatio())  : 40;
        int asgR = (gs != null && gs.getAssignmentRatio() != null) ? (int) Math.round(gs.getAssignmentRatio()) : 20;
        int attR = (gs != null && gs.getAttendanceRatio() != null) ? (int) Math.round(gs.getAttendanceRatio()) : 10;

        // 디버깅 로그 추가
        log.info("=== 점수 계산 디버깅 ===");
        log.info("입력 점수 - 중간: {}, 기말: {}, 과제: {}, 출석: {}", 
                grade.getMidExam(), grade.getFinalExam(), grade.getAssignment(), grade.getAttendance());
        log.info("비율 설정 - 중간: {}%, 기말: {}%, 과제: {}%, 출석: {}%", midR, finR, asgR, attR);
        log.info("GradeSystem 존재 여부: {}", gs != null);

        // 퍼센트를 소수로 변환하여 정확한 계산
        double totalScore = (i(grade.getMidExam()) * midR / 100.0)
                          + (i(grade.getFinalExam()) * finR / 100.0)
                          + (i(grade.getAssignment()) * asgR / 100.0)
                          + (i(grade.getAttendance()) * attR / 100.0);

        int totalInt = (int) Math.round(totalScore);  // 반올림
        totalInt = Math.max(0, Math.min(totalInt, 100));  // 0~100점 범위로 제한
        
        // 안전장치: 비율 합계가 100%가 아닌 경우 경고
        int totalRatio = midR + finR + asgR + attR;
        if (totalRatio != 100) {
            log.warn("점수 비율 합계가 100%가 아닙니다: {}% (중간:{}%, 기말:{}%, 과제:{}%, 출석:{}%)", 
                    totalRatio, midR, finR, asgR, attR);
        }
        
        log.info("계산 과정 - totalScore: {}, totalInt: {}", totalScore, totalInt);
        
        grade.setTotalInt(totalInt);
        grade.setScore((long) totalInt);      // score: Long

        // F 학점 예외 처리: 총점 30점 이하면 자동 F
        String alphabet;
        if (totalInt <= 30) {
            alphabet = "F";
            log.info("총점 30점 이하로 자동 F 처리: {}", totalInt);
        } else {
            // 퍼센트 기반 학점 분배 적용
            alphabet = calculateAlphabetByPercentage(totalInt, rulesAs, grade.getEnrollmentId());
        }
        
        grade.setAlphabet(alphabet);
        Long gpaValue = mapAlphabetToGpa10(alphabet); // gpa: Long (4.5 → 45)
        gpaValue = Math.min(gpaValue, 45L); // 최대 4.5 (45)로 제한
        grade.setGpa(gpaValue);
        
        log.info("최종 결과 - 총점: {}, 학점: {}, GPA: {}", totalInt, alphabet, gpaValue);
    }

    // 퍼센트 기반 학점 분배 계산 (5명 기준 절대평가/상대평가)
    private String calculateAlphabetByPercentage(int totalScore, List<AlphabetSystem> rules, Long enrollmentId) {
        log.info("=== 학점 계산 디버깅 ===");
        log.info("totalScore: {}, enrollmentId: {}", totalScore, enrollmentId);
        log.info("rules size: {}", rules != null ? rules.size() : "null");
        
        if (rules == null || rules.isEmpty()) {
            log.warn("규정이 없어서 F 처리");
            return "F"; // 규정이 없으면 F
        }
        
        // 해당 수강의 전체 학생 수 조회
        Long totalStudents = gradeMapper.countStudentsByEnrollment(enrollmentId);
        log.info("totalStudents: {}", totalStudents);
        if (totalStudents == null || totalStudents <= 0) {
            log.warn("학생이 없어서 F 처리");
            return "F"; // 학생이 없으면 F
        }
        
        // 5명 미만: 절대평가 적용
        if (totalStudents < 5) {
            log.info("5명 미만이므로 절대평가 적용");
            String result = calculateAbsoluteGrade(totalScore);
            log.info("절대평가 결과: {}", result);
            return result;
        }
        
        // 5명 이상: 상대평가 적용
        log.info("5명 이상이므로 상대평가 적용");
        
        // 해당 점수와 같거나 높은 점수를 받은 학생 수 조회
        Long higherScoreCount = gradeMapper.countStudentsWithHigherScore(enrollmentId, totalScore);
        if (higherScoreCount == null) {
            higherScoreCount = 0L;
        }
        
        // 상위 몇 %인지 계산 (자신과 같거나 높은 점수 포함)
        // percentile = (자신과 같거나 높은 점수를 받은 학생 수 / 전체 학생 수) * 100
        // 낮은 percentile = 상위권 (좋은 학점), 높은 percentile = 하위권 (낮은 학점)
        double percentile = ((double) higherScoreCount / totalStudents) * 100.0;
        percentile = Math.max(0.0, Math.min(100.0, percentile)); // 0-100 범위로 제한
        
        log.info("상대평가 계산 - totalScore: {}, higherScoreCount: {}, totalStudents: {}, percentile: {}%", 
                totalScore, higherScoreCount, totalStudents, percentile);
        log.info("상대평가 계산 상세 - 95점 학생의 경우: higherScoreCount={}, totalStudents={}, percentile={}%", 
                higherScoreCount, totalStudents, percentile);
        
        // 전달받은 규정 사용 (이미 applyCalcWithRules에서 적절한 규정을 선택했음)
        List<AlphabetSystem> relativeRules = rules;
        
        // 디버깅: 규정 출력
        log.info("적용할 상대평가 규정들 (총 {}개):", relativeRules != null ? relativeRules.size() : 0);
        if (relativeRules != null) {
            for (AlphabetSystem rule : relativeRules) {
                log.info("  - {}: 상위 {}%", rule.getAlphabet(), rule.getBoundary());
            }
        } else {
            log.warn("규정이 null입니다!");
        }
        
        // 규정을 오름차순으로 정렬 (A+부터 F까지) - 퍼센트 기준으로 오름차순
        // A+: 10%, A: 30%, B+: 50%, B: 70%, C+: 80%, C: 90%, D: 95% 순으로 정렬
        List<AlphabetSystem> sortedRules = new ArrayList<>(relativeRules);
        sortedRules.sort((a, b) -> {
            Double percentageA = a.getBoundary();
            Double percentageB = b.getBoundary();
            if (percentageA == null) percentageA = 0.0;
            if (percentageB == null) percentageB = 0.0;
            return Double.compare(percentageA, percentageB); // 오름차순
        });
        
        // 상위 퍼센트 기준으로 학점 결정 (구간 방식)
        // percentile이 낮을수록 상위권 (좋은 학점)
        // A+: 0% ~ 20%, A: 20% ~ 30%, B+: 30% ~ 55% 등으로 구간별 처리
        
        // 이전 규정의 퍼센트를 추적
        double prevPercentage = 0.0;
        
        for (AlphabetSystem rule : sortedRules) {
            Double percentage = rule.getBoundary();
            if (percentage == null) continue;
            
            log.info("학점 비교 - percentile: {}%, rule: {} ({}%), 구간: {}% ~ {}%", 
                    percentile, rule.getAlphabet(), percentage, prevPercentage, percentage);
            
            // percentile이 해당 구간에 속하는지 확인
            if (percentile > prevPercentage && percentile <= percentage) {
                log.info("학점 결정: {} (percentile: {}%가 구간 {}% ~ {}%에 속함)", 
                        rule.getAlphabet(), percentile, prevPercentage, percentage);
                return rule.getAlphabet();
            }
            
            prevPercentage = percentage;
        }
        
        log.warn("어떤 규정에도 해당하지 않아서 F 처리 - percentile: {}%", percentile);
        return "F"; // 기본값
    }
    
    // 절대평가 (점수 기준)
    private String calculateAbsoluteGrade(int totalScore) {
        if (totalScore >= 95) return "A+";
        else if (totalScore >= 90) return "A";
        else if (totalScore >= 85) return "B+";
        else if (totalScore >= 80) return "B";
        else if (totalScore >= 75) return "C+";
        else if (totalScore >= 70) return "C";
        else if (totalScore >= 60) return "D";
        else return "F";
    }

    // A+, A, B+, B, C+, C, D, F → GPA×10
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
        // courseId로 subjectId를 찾아서 알파벳 규정 조회
        log.info("findAlphabetBySubject 호출됨 - courseId: {}", courseId);
        
        // courseId로 subjectId 조회
        Course course = gradeMapper.findCourseById(courseId);
        if (course == null) {
            log.warn("courseId {}에 해당하는 강의를 찾을 수 없습니다.", courseId);
            return Collections.emptyList();
        }
        
        Long subjectId = course.getSubjectId();
        log.info("courseId {} -> subjectId {}", courseId, subjectId);
        
        // 해당 과목의 커스텀 규정 조회
        List<AlphabetSystem> customRules = gradeMapper.listAlphabetBySubjectAll(subjectId, null);
        log.info("커스텀 규정 조회 결과: {}개", customRules != null ? customRules.size() : 0);
        
        if (customRules != null && !customRules.isEmpty()) {
            log.info("커스텀 규정이 존재합니다.");
            
            // 학점 순서로 정렬 (A+ > A > B+ > B > C+ > C > D > F)
            customRules.sort((r1, r2) -> {
                String[] order = {"A+", "A", "B+", "B", "C+", "C", "D", "F"};
                int index1 = java.util.Arrays.asList(order).indexOf(r1.getAlphabet());
                int index2 = java.util.Arrays.asList(order).indexOf(r2.getAlphabet());
                return Integer.compare(index1, index2);
            });
            
            return customRules;
        } else {
            log.info("커스텀 규정이 없습니다. 글로벌 규정을 사용합니다.");
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
        // 총 건수 조회
        long total = gradeMapper.countSubjectRules(searchType, searchKeyword);
        
        // 페이징 처리
        int start = (req.getPage() - 1) * req.getPageSize() + 1;
        int end = req.getPage() * req.getPageSize();
        
        // 과목별 규정 상태 조회
        log.info("과목 조회 파라미터 - searchType: {}, searchKeyword: {}, start: {}, end: {}", 
                searchType, searchKeyword, start, end);
        
        List<Map<String, Object>> data = gradeMapper.findSubjectRulesStatus(searchType, searchKeyword, start, end);
        log.info("조회된 과목 데이터 수: {}", data != null ? data.size() : 0);
        
        if (data != null && !data.isEmpty()) {
            log.info("=== 과목 데이터 상세 정보 ===");
            for (int i = 0; i < data.size(); i++) {
                Map<String, Object> subject = data.get(i);
                log.info("과목 {}: {}", i + 1, subject);
                log.info("  - subjectId: {} (타입: {})", subject.get("subjectId"), 
                        subject.get("subjectId") != null ? subject.get("subjectId").getClass().getSimpleName() : "null");
                log.info("  - subjectName: {} (타입: {})", subject.get("subjectName"), 
                        subject.get("subjectName") != null ? subject.get("subjectName").getClass().getSimpleName() : "null");
            }
            log.info("=== 과목 데이터 상세 정보 끝 ===");
        } else {
            log.warn("과목 데이터가 없습니다! 데이터베이스에 과목이 등록되어 있는지 확인하세요.");
            log.warn("검색 조건: searchType={}, searchKeyword={}", searchType, searchKeyword);
        }
        
        // 기본 규정 조회 (비교용)
        List<AlphabetSystem> globalRules = getDefaultAlphabetRules();
        
        // 각 과목에 대해 규정 상태 설정
        for (Map<String, Object> subject : data) {
            // BigDecimal을 Long으로 안전하게 변환
            Object subjectIdObj = subject.get("subjectId");
            Long subjectId = null;
            if (subjectIdObj instanceof java.math.BigDecimal) {
                subjectId = ((java.math.BigDecimal) subjectIdObj).longValue();
            } else if (subjectIdObj instanceof Number) {
                subjectId = ((Number) subjectIdObj).longValue();
            }
            
            log.info("Processing subject - subjectId: {}, subjectName: {}", subjectId, subject.get("subjectName"));
            log.info("Raw subject data: {}", subject);
            
            // 해당 과목의 커스텀 규정이 있는지 확인
            List<AlphabetSystem> customRules = null;
            if (subjectId != null) {
                customRules = gradeMapper.listAlphabetBySubjectAll(subjectId, null);
            }
            log.info("Custom rules found for subject {}: {}", subjectId, customRules != null ? customRules.size() : 0);
            

            if (customRules != null && !customRules.isEmpty()) {
                // 커스텀 규정이 있으면 글로벌 규정과 비교
                boolean isDifferentFromGlobal = false;
                Map<String, Double> globalRuleMap = new HashMap<>();
                
                // 글로벌 규정을 Map으로 변환
                for (AlphabetSystem globalRule : globalRules) {
                    globalRuleMap.put(globalRule.getAlphabet(), globalRule.getBoundary());
                }
                
                // 커스텀 규정과 글로벌 규정 비교
                for (AlphabetSystem customRule : customRules) {
                    Double globalValue = globalRuleMap.get(customRule.getAlphabet());
                    if (globalValue != null && !globalValue.equals(customRule.getBoundary())) {
                        isDifferentFromGlobal = true;
                        log.info("커스텀 규정 감지 - 학점: {}, 커스텀: {}%, 글로벌: {}%", 
                                customRule.getAlphabet(), customRule.getBoundary(), globalValue);
                        break;
                    }
                }
                
                if (isDifferentFromGlobal) {
                    subject.put("ruleType", "CUSTOM");
                    subject.put("rules", customRules);
                    log.info("CUSTOM 규정 설정 - 과목 {}: 글로벌과 다른 커스텀 규정", subjectId);
                } else {
                    subject.put("ruleType", "GLOBAL");
                    subject.put("rules", globalRules);
                    log.info("GLOBAL 규정 설정 - 과목 {}: 글로벌과 동일한 커스텀 규정", subjectId);
                }
            } else {
                // 커스텀 규정이 없으면 글로벌로 설정
                subject.put("ruleType", "GLOBAL");
                subject.put("rules", globalRules);
                log.info("GLOBAL 규정 설정 - 과목 {}: 커스텀 규정 없음", subjectId);
            }
        }
        
        log.info("=== PageResponseDTO 생성 전 ===");
        log.info("data 크기: {}", data != null ? data.size() : 0);
        log.info("total: {}", total);
        log.info("req: {}", req);
        
        PageResponseDTO<Map<String, Object>> result = PageResponseDTO.pageOf(data, total, req);
        
        log.info("=== PageResponseDTO 생성 후 ===");
        log.info("result: {}", result);
        log.info("result.getData() 크기: {}", result.getData() != null ? result.getData().size() : 0);
        
        return result;
    }


}
