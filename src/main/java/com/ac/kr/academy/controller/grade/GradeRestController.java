package com.ac.kr.academy.controller.grade;

import com.ac.kr.academy.domain.grade.AlphabetSystem;
import com.ac.kr.academy.domain.grade.Grade;
import com.ac.kr.academy.domain.grade.GradeSystem;
import com.ac.kr.academy.dto.page.PageRequestDTO;
import com.ac.kr.academy.dto.page.PageResponseDTO;
import com.ac.kr.academy.security.CustomUserDetails;
import com.ac.kr.academy.service.grade.GradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/grade")
public class GradeRestController {

    private final GradeService gradeService;

    // 글로벌 규정 전체 조회 (페이징 메서드를 큰 페이지로 감싸서 사용)
    @GetMapping("/alphabet/global")
    public ResponseEntity<List<AlphabetSystem>> getAlphabetGlobalAll(
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String searchKeyword) {

        PageRequestDTO req = new PageRequestDTO();
        req.setPage(1);
        req.setPageSize(1000); // 충분히 큰 값

        PageResponseDTO<AlphabetSystem> page =
                gradeService.listAlphabetGlobal(searchType, searchKeyword, req);

        return ResponseEntity.ok(page.getData());
    }

    // 과목/수강 전용 규정 전체 조회
    @GetMapping("/alphabet/subject")
    public ResponseEntity<List<AlphabetSystem>> getAlphabetBySubjectAll(
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long enrollmentId,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String searchKeyword) {

        PageRequestDTO req = new PageRequestDTO();
        req.setPage(1);
        req.setPageSize(1000);

        PageResponseDTO<AlphabetSystem> page =
                gradeService.listAlphabetBySubject(subjectId, enrollmentId, searchType, searchKeyword, req);

        return ResponseEntity.ok(page.getData());
    }

    // 코스별 점수 가중치 단건 조회 (없으면 204)
    @GetMapping("/system")
    public ResponseEntity<GradeSystem> getGradeSystem(@RequestParam Long courseId) {
        GradeSystem gs = gradeService.getGradeSystemByCourse(courseId);
        if (gs == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(gs);
    }

    // 성적 미리보기 계산 (저장하지 않음)
    @PostMapping("/preview")
    public ResponseEntity<Map<String, Object>> preview(@RequestBody Grade grade) {
        Map<String, Object> result = gradeService.calculateGradePreview(grade);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 학생 집계: 총 취득학점, 평균 GPA
    @GetMapping("/student/{studentId}/summary")
    public ResponseEntity<Map<String, Object>> summarize(@PathVariable Long studentId) {
        return ResponseEntity.ok(gradeService.summarizeForStudent(studentId));
    }

    // 졸업요건 체크 (요건 파라미터 전달)
    @GetMapping("/student/{studentId}/graduation")
    public ResponseEntity<Map<String, Object>> graduation(
            @PathVariable Long studentId,
            @RequestParam long requiredScore,
            @RequestParam double requiredAvgGpa) {
        return ResponseEntity.ok(gradeService.checkGraduation(studentId, requiredScore, requiredAvgGpa));
    }

    // ================== 글로벌 규정 관리 REST API ==================

    // 글로벌 규정 저장 (AJAX용)
    @PostMapping("/admin/global-rules/save")
    public ResponseEntity<String> saveGlobalRules(@RequestParam Map<String, String> params) {
        log.info("글로벌 규정 저장 요청 받음: {}", params);
        try {
            gradeService.saveGlobalRules(params);
            log.info("글로벌 규정 저장 성공");
            return ResponseEntity.ok("저장이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            log.error("글로벌 규정 저장 검증 오류: {}", e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            log.error("글로벌 규정 저장 중 예상치 못한 오류", e);
            return ResponseEntity.status(500).body("저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // ================== 과목별 규정 관리 REST API ==================

    // 과목별 규정 초기화 (글로벌 규정으로 되돌리기)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/subject-rules/reset")
    public ResponseEntity<String> resetSubjectRules(@RequestParam Long subjectId) {
        try {
            gradeService.resetSubjectToGlobal(subjectId);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("ERROR");
        }
    }

    // 과목별 규정 인라인 저장
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/subject-rules/save")
    public ResponseEntity<String> saveSubjectRulesInline(@RequestParam Map<String, String> params) {
        log.info("과목별 규정 저장 요청 받음: {}", params);
        try {
            gradeService.saveSubjectRulesInline(params);
            log.info("과목별 규정 저장 성공");
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            log.error("과목별 규정 저장 중 오류", e);
            return ResponseEntity.status(500).body("ERROR: " + e.getMessage());
        }
    }

    // 커스텀 규정 생성 (글로벌 규정 복사)
    @PostMapping("/admin/subject-rules/create-custom")
    public ResponseEntity<String> createCustomRules(@RequestParam Long subjectId) {
        try {
            gradeService.createCustomRulesFromGlobal(subjectId);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("ERROR");
        }
    }

    // ================== 교수 성적 관리 REST API ==================

    // 성적 등록 (AJAX용)
    @PostMapping("/professor/add")
    public ResponseEntity<String> addGrade(@RequestParam Long enrollmentId,
                                          @RequestParam Integer midExam,
                                          @RequestParam Integer finalExam,
                                          @RequestParam Integer assignment,
                                          @RequestParam Integer attendance,
                                          @RequestParam(required = false) Long professorId,
                                          @RequestParam Long courseId,
                                          @RequestParam(required = false) Long subjectId,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("=== 성적 등록 요청 받음 ===");
        log.info("enrollmentId: {}, midExam: {}, finalExam: {}, assignment: {}, attendance: {}", 
                enrollmentId, midExam, finalExam, assignment, attendance);
        log.info("professorId: {}, courseId: {}, subjectId: {}", professorId, courseId, subjectId);

        try {
            // professorId가 없으면 로그인한 사용자에서 가져오기
            Long profId = professorId;
            if (profId == null) {
                Long userId = userDetails.getUserId();
                profId = gradeService.findProfessorIdByUserId(userId);
                log.info("로그인한 사용자에서 professorId 가져옴: {}", profId);
            }

            Grade grade = new Grade();
            grade.setEnrollmentId(enrollmentId);
            grade.setMidExam(midExam);
            grade.setFinalExam(finalExam);
            grade.setAssignment(assignment);
            grade.setAttendance(attendance);

            log.info("Grade 객체 생성 완료 - Grade: {}", grade);
            
            gradeService.addGrade(grade, profId);
            
            log.info("성적 등록 성공 - enrollmentId: {}, professorId: {}", enrollmentId, professorId);
            log.info("최종 Grade 정보 - 총점: {}, 학점: {}, GPA: {}", 
                    grade.getTotalInt(), grade.getAlphabet(), grade.getGpa());
            
            String responseMessage = String.format("성적이 성공적으로 등록되었습니다. 총점: %d, 학점: %s",
                grade.getTotalInt(), grade.getAlphabet());
            
            log.info("응답 메시지: {}", responseMessage);
            return ResponseEntity.ok(responseMessage);

        } catch (Exception e) {
            log.error("성적 등록 실패 - enrollmentId: {}, professorId: {}", enrollmentId, professorId, e);
            String errorMessage = "성적 등록에 실패했습니다: " + e.getMessage();
            log.error("에러 응답 메시지: {}", errorMessage);
            return ResponseEntity.status(500).body(errorMessage);
        }
    }

    // 성적 수정 (AJAX용)
    @PostMapping("/professor/edit")
    public ResponseEntity<String> editGrade(@RequestParam String id,
                                           @RequestParam String enrollmentId,
                                           @RequestParam String midExam,
                                           @RequestParam String finalExam,
                                           @RequestParam String assignment,
                                           @RequestParam String attendance,
                                           @RequestParam(required = false) String professorId,
                                           @RequestParam String courseId,
                                           @RequestParam(required = false) String subjectId,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("=== editGrade 메서드 호출됨 ===");
        log.info("성적 수정 요청 - id: {}, enrollmentId: {}, professorId: {}, courseId: {}, subjectId: {}",
                id, enrollmentId, professorId, courseId, subjectId);

        try {
            // String을 Long으로 변환
            Long gradeId = Long.parseLong(id);
            Long profId = (professorId != null && !professorId.isEmpty()) ? Long.parseLong(professorId) : null;
            
            // professorId가 없으면 로그인한 사용자에서 가져오기
            if (profId == null) {
                Long userId = userDetails.getUserId();
                profId = gradeService.findProfessorIdByUserId(userId);
                log.info("로그인한 사용자에서 professorId 가져옴: {}", profId);
            }
            Long courseIdLong = Long.parseLong(courseId);
            Long subjectIdLong = (subjectId != null && !subjectId.isEmpty()) ? Long.parseLong(subjectId) : null;

            // Grade 객체 생성 및 설정
            Grade grade = new Grade();
            grade.setId(gradeId);
            grade.setEnrollmentId(Long.parseLong(enrollmentId));
            grade.setMidExam(Integer.parseInt(midExam));
            grade.setFinalExam(Integer.parseInt(finalExam));
            grade.setAssignment(Integer.parseInt(assignment));
            grade.setAttendance(Integer.parseInt(attendance));

            gradeService.editGrade(grade, profId);

            log.info("성적 수정 성공 - gradeId: {}, professorId: {}", gradeId, profId);
            return ResponseEntity.ok(String.format("성적이 성공적으로 수정되었습니다. 총점: %d, 학점: %s",
                grade.getTotalInt(), grade.getAlphabet()));

        } catch (NumberFormatException e) {
            log.error("숫자 변환 오류", e);
            return ResponseEntity.status(400).body("잘못된 데이터 형식입니다.");
        } catch (Exception e) {
            log.error("성적 수정 실패 - id: {}, professorId: {}", id, professorId, e);
            return ResponseEntity.status(500).body("성적 수정에 실패했습니다: " + e.getMessage());
        }
    }

}
