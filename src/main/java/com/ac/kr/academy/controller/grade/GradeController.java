package com.ac.kr.academy.controller.grade;

import com.ac.kr.academy.domain.grade.AlphabetSystem;
import com.ac.kr.academy.domain.grade.Grade;
import com.ac.kr.academy.domain.grade.GradeSystem;
import com.ac.kr.academy.domain.subject.Subject;
import com.ac.kr.academy.dto.page.PageRequestDTO;
import com.ac.kr.academy.dto.page.PageResponseDTO;
import com.ac.kr.academy.dto.user.MyPageInfoDTO;
import com.ac.kr.academy.security.CustomUserDetails;
import com.ac.kr.academy.service.grade.GradeService;
import com.ac.kr.academy.service.user.UserService;
import com.ac.kr.academy.mapper.grade.GradeMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/grade")
public class GradeController {

    private final GradeService gradeService;
    private final GradeMapper gradeMapper;
    private final UserService userService;

    /*================================ 관리자 : 규정 목록/ CRUD ================================*/

    // 전체 규정 조회 및 수정 페이지
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/rule-global")
    public String adminGlobalRules(Model model) {
        
        log.info("=== 관리자 글로벌 규정 페이지 접근 ===");
        
        // 실제 데이터베이스에서 글로벌 규정 조회
        PageRequestDTO req = new PageRequestDTO();
        req.setPage(1);
        req.setPageSize(100); // 충분히 큰 값
        
        PageResponseDTO<AlphabetSystem> result = gradeService.listAlphabetGlobal(null, null, req);
        log.info("DB에서 조회된 글로벌 규정 수: {}", result != null && result.getData() != null ? result.getData().size() : 0);
        
        // DB에 글로벌 규정이 없으면 기본값 생성
        if (result == null || result.getData() == null || result.getData().isEmpty()) {
            log.info("DB에 글로벌 규정이 없어서 기본값 생성");
            List<AlphabetSystem> defaultRules = new ArrayList<>();
            String[] grades = {"A+", "A", "B+", "B", "C+", "C", "D"};
            double[] boundaries = {15.0, 30.0, 50.0, 70.0, 75.0, 80.0, 95.0};
            
            for (int i = 0; i < grades.length; i++) {
                AlphabetSystem rule = new AlphabetSystem();
                rule.setAlphabet(grades[i]);
                rule.setBoundary(boundaries[i]);
                rule.setCourseId(null); // 글로벌 규정
                defaultRules.add(rule);
            }
            
            result = new PageResponseDTO<>();
            result.setData(defaultRules);
            result.setTotalCount(defaultRules.size());
            result.setCurrentPage(1);
            result.setPageSize(10);
            result.setTotalPage(1);
            result.setStartPage(1);
            result.setEndPage(1);
            result.setHasPrevious(false);
            result.setHasNext(false);
        }
        
        model.addAttribute("result", result);
        return "grade/admin/admin-rule-global";
    }

    // 삭제 (POST)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/alphabet/delete")
    public String adminAlphabetDelete(@RequestParam Long id) {
        gradeService.deleteAlphabetRule(id);
        return "redirect:/grade/admin/rule-global";
    }
    
    // 글로벌 규정 초기화(기본값 재생성)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/rule-global/init")
    public String initGlobalRules() {
        try {
            // 기존 글로벌 규정 삭제
            gradeService.getGradeMapper().deleteAlphabetGlobal();
            
            // 기본 상대평가 규정 생성
            String[] grades = {"A+", "A", "B+", "B", "C+", "C", "D"};
            double[] boundaries = {15.0, 30.0, 50.0, 70.0, 75.0, 80.0, 95.0};
            
            for (int i = 0; i < grades.length; i++) {
                AlphabetSystem rule = new AlphabetSystem();
                rule.setAlphabet(grades[i]);
                rule.setBoundary(boundaries[i]);
                rule.setCourseId(null); // 글로벌 규정
                gradeService.getGradeMapper().insertAlphabetGlobal(rule);
            }
            
            // 글로벌 규정 초기화 후 모든 과목에 자동 적용 (기존 메서드 활용)
            try {
                List<Subject> allSubjects = gradeService.getAllSubjects();
                
                if (allSubjects != null && !allSubjects.isEmpty()) {
                    for (Subject subject : allSubjects) {
                        try {
                            gradeService.resetSubjectToGlobal(subject.getId());
                        } catch (Exception e) {
                            log.error("과목 ID {}에 글로벌 규정 적용 실패", subject.getId(), e);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("모든 과목에 글로벌 규정 적용 중 오류", e);
            }
            
        } catch (Exception e) {
            log.error("글로벌 규정 초기화 실패", e);
        }
        
        return "redirect:/grade/admin/rule-global";
    }
    

    // 과목별 규정 목록 (검색/페이징)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/subject-rules")
    public String subjectRulesList(@RequestParam(required = false) String searchType,
                                   @RequestParam(required = false) String searchKeyword,
                                   @ModelAttribute PageRequestDTO req,
                                   Model model) {
        
        log.info("=== 관리자 과목별 규정 페이지 접근 ===");
        
        // 과목별 규정 상태 조회
        PageResponseDTO<Map<String, Object>> result = 
                gradeService.listSubjectRulesStatus(searchType, searchKeyword, req);
        
        model.addAttribute("req", req);
        model.addAttribute("result", result);
        
        // 실제 DB에서 글로벌 규정 조회 (subject-rules-list.jsp에서 사용)
        Map<String, Double> globalRules = new HashMap<>();
        List<AlphabetSystem> defaultRules = new ArrayList<>();
        
        try {
            // 실제 DB에서 글로벌 규정 조회
            List<AlphabetSystem> dbGlobalRules = gradeService.getGradeMapper().findGlobalAlphabetRules();
            
            if (dbGlobalRules != null && !dbGlobalRules.isEmpty()) {
                // 실제 DB의 글로벌 규정 사용
                for (AlphabetSystem rule : dbGlobalRules) {
                    globalRules.put(rule.getAlphabet(), rule.getBoundary());
                    defaultRules.add(rule);
                }
            } else {
                // DB에 글로벌 규정이 없으면 기본값 사용
                log.warn("DB 글로벌 규정 없음: 기본값 사용");
                String[] grades = {"A+", "A", "B+", "B", "C+", "C", "D"};
                double[] boundaries = {15.0, 30.0, 50.0, 70.0, 75.0, 80.0, 95.0};
                
                for (int i = 0; i < grades.length; i++) {
                    AlphabetSystem rule = new AlphabetSystem();
                    rule.setAlphabet(grades[i]);
                    rule.setBoundary(boundaries[i]);
                    rule.setCourseId(null);
                    defaultRules.add(rule);
                    globalRules.put(grades[i], boundaries[i]);
                }
            }
        } catch (Exception e) {
            log.error("글로벌 규정 조회 오류", e);
            // 오류 발생 시 기본값 사용
            String[] grades = {"A+", "A", "B+", "B", "C+", "C", "D"};
            double[] boundaries = {15.0, 30.0, 50.0, 70.0, 75.0, 80.0, 95.0};
            
            for (int i = 0; i < grades.length; i++) {
                AlphabetSystem rule = new AlphabetSystem();
                rule.setAlphabet(grades[i]);
                rule.setBoundary(boundaries[i]);
                rule.setCourseId(null);
                defaultRules.add(rule);
                globalRules.put(grades[i], boundaries[i]);
            }
        }
        
        model.addAttribute("globalRules", globalRules);
        model.addAttribute("defaultRules", defaultRules);
        
        // 검색 파라미터 유지
        Map<String, Object> keep = new HashMap<>();
        if (searchType != null && !searchType.isBlank()) keep.put("searchType", searchType);
        if (searchKeyword != null && !searchKeyword.isBlank()) keep.put("searchKeyword", searchKeyword);
        model.addAttribute("keepParams", keep);
        
        return "grade/admin/admin-subject-rules";
    }

    // 디버그: 과목 데이터 직접 확인 (운영 비노출 권장)
    @GetMapping("/admin/debug/subjects")
    @ResponseBody
    public String debugSubjects() {
        try {
            // 매퍼를 직접 호출해서 과목 데이터 확인
            List<Map<String, Object>> subjects = gradeService.getGradeMapper().findSubjectRulesStatus(null, null, 1, 10);
            
            StringBuilder result = new StringBuilder();
            result.append("=== DB 과목 데이터 확인 ===\n");
            result.append("조회된 과목 수: ").append(subjects != null ? subjects.size() : 0).append("\n\n");
            
            if (subjects != null && !subjects.isEmpty()) {
                for (int i = 0; i < subjects.size(); i++) {
                    Map<String, Object> subject = subjects.get(i);
                    result.append("과목 ").append(i + 1).append(":\n");
                    result.append("  - ID: ").append(subject.get("subjectId")).append("\n");
                    result.append("  - 이름: ").append(subject.get("subjectName")).append("\n");
                    result.append("  - 학점: ").append(subject.get("credit")).append("\n");
                    result.append("  - 전체 데이터: ").append(subject).append("\n\n");
                }
            } else {
                result.append("과목 데이터 없음\n");
            }
            
            return result.toString();
        } catch (Exception e) {
            return "에러 발생: " + e.getMessage() + "\n" + e.getStackTrace()[0].toString();
        }
    }

    // 테스트 페이지
    @GetMapping("/admin/test-page")
    public String testPage() {
        return "grade/admin/test-page";
    }

    // 디버그: 교수 성적 목록 데이터 확인 (운영 비노출 권장)
    @GetMapping("/professor/debug/list")
    @ResponseBody
    public String debugProfessorList(@RequestParam Long professorId,
                                     @RequestParam(required = false) Long courseId,
                                     @RequestParam(required = false) Long subjectId) {
        try {
            PageRequestDTO req = new PageRequestDTO();
            req.setPage(1);
            req.setPageSize(10);
            
            PageResponseDTO<Grade> result = gradeService.listByCourse(professorId, courseId, subjectId, null, null, req);
            
            StringBuilder debug = new StringBuilder();
            debug.append("=== 교수 성적 목록 디버그 ===\n");
            debug.append("professorId: ").append(professorId).append("\n");
            debug.append("courseId: ").append(courseId).append("\n");
            debug.append("subjectId: ").append(subjectId).append("\n");
            debug.append("총 데이터 수: ").append(result.getTotalCount()).append("\n\n");
            
            if (result.getData() != null && !result.getData().isEmpty()) {
                for (Grade grade : result.getData()) {
                    debug.append("Grade ID: ").append(grade.getId()).append("\n");
                    debug.append("  - enrollmentId: ").append(grade.getEnrollmentId()).append("\n");
                    debug.append("  - studentId: ").append(grade.getStudentId()).append("\n");
                    debug.append("  - studentName: ").append(grade.getStudentName()).append("\n");
                    debug.append("  - studentNo: ").append(grade.getStudentNo()).append("\n");
                    debug.append("  - subjectName: ").append(grade.getSubjectName()).append("\n");
                    debug.append("  - midExam: ").append(grade.getMidExam()).append("\n");
                    debug.append("  - finalExam: ").append(grade.getFinalExam()).append("\n");
                    debug.append("  - assignment: ").append(grade.getAssignment()).append("\n");
                    debug.append("  - attendance: ").append(grade.getAttendance()).append("\n");
                    debug.append("  - totalInt: ").append(grade.getTotalInt()).append("\n");
                    debug.append("  - alphabet: ").append(grade.getAlphabet()).append("\n");
                    debug.append("  - gpa: ").append(grade.getGpa()).append("\n\n");
                }
            } else {
                debug.append("데이터 없음\n");
            }
            
            return debug.toString();
        } catch (Exception e) {
            return "에러 발생: " + e.getMessage() + "\n" + e.getStackTrace()[0].toString();
        }
    }


    // 특정 과목 규정 목록
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/rule/subject")
    public String listAlphabetSubject(@RequestParam(required = false) Long subjectId,
                                      @RequestParam(required = false) Long enrollmentId,
                                      @ModelAttribute PageRequestDTO req,
                                      @RequestParam(required = false) String searchType,
                                      @RequestParam(required = false) String searchKeyword,
                                      Model model) {

        PageResponseDTO<AlphabetSystem> result = gradeService.listAlphabetBySubject(
                subjectId, enrollmentId, searchType, searchKeyword, req   // ★ 누락된 2개 추가
        );

        model.addAttribute("result", result);
        model.addAttribute("req", req);

        // 페이지 이동 시 유지할 값
        Map<String, Object> keep = new HashMap<>();
        if (subjectId != null) keep.put("subjectId", subjectId);
        if (enrollmentId != null) keep.put("enrollmentId", enrollmentId);
        model.addAttribute("keepParams", keep);

        return "grade/admin/admin-rule-global";
    }


    // 삭제
    @PostMapping("/admin/rule/subject/delete")
    public String adminRuleSubjectDelete(@RequestParam Long id,
                                         @RequestParam(required = false) Long courseId) {
        gradeService.deleteAlphabetRule(id);
        String redirect = "/grade/admin/rule/subject";
        redirect += (courseId != null ? "?courseId=" + courseId : "");
        return "redirect:" + redirect;
    }

    /*================================교수 : 성적 목록/CRUD================================*/
    // 교수 강의 목록 (1단계)
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADVISOR')")
    @GetMapping("/professor/courses")
    public String professorCourses(@RequestParam(required = false) String searchType,
                                   @RequestParam(required = false) String searchKeyword,
                                   @ModelAttribute PageRequestDTO req,
                                   @AuthenticationPrincipal CustomUserDetails userDetails,
                                   Model model) {
        
        // 로그인한 사용자 정보에서 professorId 가져오기
        Long userId = userDetails.getUserId();
        Long professorId = gradeService.findProfessorIdByUserId(userId);
        
        // CourseService를 활용하여 교수가 개설한 강의 목록 조회
        PageResponseDTO<Map<String, Object>> result = 
                gradeService.listProfessorCoursesFromCourseService(professorId, searchType, searchKeyword, req);

        model.addAttribute("result", result);
        model.addAttribute("req", req);
        model.addAttribute("professorId", professorId);

        Map<String, Object> keep = new HashMap<>();
        keep.put("professorId", professorId);
        model.addAttribute("keepParams", keep);

        return "grade/professor/professor-courses";
    }

    // 성적 목록 (교수) - 2단계: 특정 강의의 학생 목록
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADVISOR')")
    @GetMapping("/professor/list")
    public String ListForProfessor(@RequestParam(required = false) Long professorId,
                                   @RequestParam(required = false) Long courseId,
                                   @RequestParam(required = false) Long subjectId,
                                   @RequestParam(required = false) String searchType,
                                   @RequestParam(required = false) String searchKeyword,
                                   @ModelAttribute PageRequestDTO req,
                                   @AuthenticationPrincipal CustomUserDetails userDetails,
                                   Model model
    ) {

        // professorId가 없으면 로그인한 사용자에서 가져오기
        if (professorId == null) {
            Long userId = userDetails.getUserId();
            professorId = gradeService.findProfessorIdByUserId(userId);
        }

        // courseId가 없으면 강의 목록으로 리다이렉트
        if (courseId == null) {
            log.warn("courseId가 null입니다. 강의 목록으로 리다이렉트합니다.");
            return "redirect:/grade/professor/courses";
        }
        
        log.info("수강생 관리 페이지 접근 - professorId: {}, courseId: {}, subjectId: {}", professorId, courseId, subjectId);

        // 서비스 호출 -> 페이지 결과 수신 (기존 메서드 사용)
        PageResponseDTO<Grade> result =
                gradeService.listByCourse(professorId, courseId, subjectId, searchType, searchKeyword, req);
        
        if (result.getTotalCount() == 0) {
            log.warn("성적 데이터 없음 - courseId: {}, 수강신청한 학생이 없거나 성적이 등록되지 않음", courseId);
        } else {
            log.info("성적 데이터 조회 성공 - courseId: {}, 총 {}명의 수강생", courseId, result.getTotalCount());
        }

        // 🔥 수정: 상대평가 규정만 조회 (절대평가는 하드코딩)
        List<AlphabetSystem> relativeRules = null;
        
        // 1단계: 과목별 커스텀 상대평가 규정 확인
        if (subjectId != null) {
            relativeRules = gradeService.getGradeMapper().listAlphabetBySubjectAll(subjectId, null);
            log.info("과목별 커스텀 상대평가 규정 조회 결과: {}개", relativeRules != null ? relativeRules.size() : 0);
        }
        
        // 2단계: 커스텀 규정이 없으면 글로벌 상대평가 규정 사용
        if (relativeRules == null || relativeRules.isEmpty()) {
            relativeRules = gradeService.getGradeMapper().findGlobalAlphabetRules();
            log.info("글로벌 상대평가 규정 조회 결과: {}개", relativeRules != null ? relativeRules.size() : 0);
        }
        
        // 3단계: 상대평가 규정 정렬 (A+부터 순서대로)
        if (relativeRules != null && !relativeRules.isEmpty()) {
            String[] gradeOrder = {"A+", "A", "B+", "B", "C+", "C", "D"};
            relativeRules.sort((a, b) -> {
                int indexA = java.util.Arrays.asList(gradeOrder).indexOf(a.getAlphabet());
                int indexB = java.util.Arrays.asList(gradeOrder).indexOf(b.getAlphabet());
                return Integer.compare(indexA, indexB);
            });
            log.info("상대평가 규정 정렬 완료 - A+부터 순서대로");
        }
        
        // 점수 배율 조회
        GradeSystem gradeSystem = gradeService.getGradeSystemByCourse(courseId);

        model.addAttribute("result", result);
        model.addAttribute("req", req);
        model.addAttribute("professorId", professorId);
        model.addAttribute("relativeRules", relativeRules);  // 상대평가 규정만 전달
        model.addAttribute("gradeSystem", gradeSystem);

        Map<String, Object> keep = new HashMap<>();
        keep.put("professorId", professorId);
        if (courseId != null) keep.put("courseId", courseId);
        if (subjectId != null) keep.put("subjectId", subjectId);
        model.addAttribute("keepParams", keep);

        return "grade/professor/professor-list";
    }

    // 전체 학생 학점 재계산 (AJAX)
    @PostMapping("/professor/recalculate-all")
    @ResponseBody
    public String recalculateAllGrades(@RequestParam Long courseId) {
        log.info("=== 전체 학점 재계산 요청 ===");
        log.info("courseId: {}", courseId);
        
        try {
            gradeService.recalculateAllGradesForCourse(courseId);
            log.info("전체 학점 재계산 완료");
            return "success";
        } catch (Exception e) {
            log.error("전체 학점 재계산 실패", e);
            return "error: " + e.getMessage();
        }
    }


    // 학생 선택 폼 (성적 등록 전 단계)
    @GetMapping("/professor/add-student")
    public String professorGradeAddStudentForm(@RequestParam Long courseId,
                                               @RequestParam Long subjectId,
                                               @RequestParam Long professorId,
                                               Model model) {
        // 해당 강의의 수강생 목록 조회 (성적이 없는 학생들)
        List<Map<String, Object>> students = gradeService.getStudentsWithoutGrade(courseId);
        
        model.addAttribute("students", students);
        model.addAttribute("courseId", courseId);
        model.addAttribute("subjectId", subjectId);
        model.addAttribute("professorId", professorId);

        return "grade/professor/add-student";
    }

    // 등록 폼
    @GetMapping("/professor/add")
    public String professorGradeAddForm(@RequestParam Long courseId,
                                        @RequestParam Long subjectId,
                                        @RequestParam Long enrollmentId,
                                        Model model) {
        Grade g = new Grade();
        g.setEnrollmentId(enrollmentId);

        model.addAttribute("g", g);
        model.addAttribute("courseId", courseId);
        model.addAttribute("subjectId", subjectId);

        return "grade/professor/add";

    }

    // 등록 처리 (폼 제출용) - REST API로 이동됨

    // 수정 폼
    @GetMapping("/professor/edit")
    public String professorGradeEditForm(@RequestParam Long id,
                                         @RequestParam Long courseId,
                                         @RequestParam Long subjectId,
                                         Model model) {
        // 단건조회 메서드가 따로 없으면 getMyGrade로 대체(학생ID null 허용)
        Grade g = gradeService.getMyGrade(null, id);
        model.addAttribute("g", g);
        model.addAttribute("courseId", courseId);
        model.addAttribute("subjectId", subjectId);
        return "grade/professor/edit";
    }

    // 수정 처리
    @PostMapping("/professor/edit")
    @ResponseBody
    public String professorGradeEdit(@RequestParam String id,
                                     @RequestParam String enrollmentId,
                                     @RequestParam String midExam,
                                     @RequestParam String finalExam,
                                     @RequestParam String assignment,
                                     @RequestParam String attendance,
                                     @RequestParam(required = false) String professorId,
                                     @RequestParam String courseId,
                                     @RequestParam String subjectId) {
        
        log.info("=== professorGradeEdit 메서드 호출됨 ===");
        log.info("성적 수정 요청 - id: {}, enrollmentId: {}, professorId: {}, courseId: {}, subjectId: {}", 
                id, enrollmentId, professorId, courseId, subjectId);
        
        try {
            // String을 Long으로 변환
            Long gradeId = Long.parseLong(id);
            Long profId = (professorId != null && !professorId.isEmpty()) ? Long.parseLong(professorId) : null;
            Long courseIdLong = Long.parseLong(courseId);
            Long subjectIdLong = Long.parseLong(subjectId);
            
            // Grade 객체 생성 및 설정
            Grade grade = new Grade();
            grade.setId(gradeId);
            grade.setEnrollmentId(Long.parseLong(enrollmentId)); // enrollmentId 설정 추가!
            
            // 점수 변환 및 설정
            int midExamInt = Integer.parseInt(midExam);
            int finalExamInt = Integer.parseInt(finalExam);
            int assignmentInt = Integer.parseInt(assignment);
            int attendanceInt = Integer.parseInt(attendance);
            
            grade.setMidExam(Math.min(midExamInt, 100)); // 최대 100으로 제한
            grade.setFinalExam(Math.min(finalExamInt, 100)); // 최대 100으로 제한
            grade.setAssignment(Math.min(assignmentInt, 100)); // 최대 100으로 제한
            grade.setAttendance(Math.min(attendanceInt, 100)); // 최대 100으로 제한
            
            log.info("점수 설정 완료 - 중간: {}, 기말: {}, 과제: {}, 출석: {}", 
                    grade.getMidExam(), grade.getFinalExam(), grade.getAssignment(), grade.getAttendance());
            
            gradeService.editGrade(grade, profId);
            log.info("성적 수정 완료 - id: {}, 총점: {}, 학점: {}", gradeId, grade.getTotalInt(), grade.getAlphabet());
            return "success";
        } catch (NumberFormatException e) {
            log.error("숫자 변환 오류: {}", e.getMessage());
            return "error: 숫자 변환 오류";
        } catch (Exception e) {
            log.error("성적 수정 실패 - id: {}, error: {}", id, e.getMessage());
            return "error: " + e.getMessage();
        }
    }

    // 삭제 (AJAX용)
    @PostMapping("/professor/delete")
    @ResponseBody
    public ResponseEntity<String> professorGradeDelete(@RequestParam String id,
                                                      @RequestParam(required = false) String professorId) {
        
        log.info("성적 삭제 요청 - id: {}, professorId: {}", id, professorId);
        
        try {
            // String을 Long으로 변환
            Long gradeId = Long.parseLong(id);
            Long profId = (professorId != null && !professorId.isEmpty()) ? Long.parseLong(professorId) : null;
            
            gradeService.deleteGrade(gradeId, profId);
            log.info("성적 삭제 완료 - id: {}", gradeId);
            
            return ResponseEntity.ok("성적이 성공적으로 삭제되었습니다.");
            
        } catch (NumberFormatException e) {
            log.error("숫자 변환 오류: {}", e.getMessage());
            return ResponseEntity.status(400).body("잘못된 데이터 형식입니다.");
        } catch (Exception e) {
            log.error("성적 삭제 실패 - id: {}, error: {}", id, e.getMessage());
            return ResponseEntity.status(500).body("성적 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /*================================교수 : 규정 조회 ================================*/
    // 테스트용 간단한 페이지
    @GetMapping("/professor/test")
    public String professorTest(Model model) {
        model.addAttribute("message", "교수 페이지가 정상적으로 작동합니다!");
        return "grade/professor/test";
    }
    
    
    // 교수용 글로벌 규정 조회 (읽기 전용)

    // 교수용 과목별 규정 조회 (읽기 전용)
    @GetMapping("/professor/rule/subject/{subjectId}")
    public String professorViewSubjectRules(@PathVariable Long subjectId, Model model) {
        try {
            // 간단한 과목별 규정 조회 로직
            model.addAttribute("subjectId", subjectId);
            model.addAttribute("message", "과목별 규정 조회 기능입니다.");
            return "grade/professor/subject-rules-view";
        } catch (Exception e) {
            model.addAttribute("error", "과목별 규정을 불러오는 중 오류가 발생했습니다: " + e.getMessage());
            return "grade/professor/subject-rules-view";
        }
    }

    /*================================REST API (AJAX용)================================*/
    // AJAX 성적 등록
    @PostMapping("/professor/add-ajax")
    @ResponseBody
    public String professorGradeAddAjax(@RequestParam Long enrollmentId,
                                       @RequestParam Long courseId,
                                       @RequestParam Long subjectId,
                                       @RequestParam Integer midExam,
                                       @RequestParam Integer finalExam,
                                       @RequestParam Integer assignment,
                                       @RequestParam Integer attendance,
                                       @RequestParam Long professorId) {
        try {
            Grade grade = new Grade();
            grade.setEnrollmentId(enrollmentId);
            grade.setMidExam(midExam);
            grade.setFinalExam(finalExam);
            grade.setAssignment(assignment);
            grade.setAttendance(attendance);
            
            gradeService.addGrade(grade, professorId);
            return "success";
        } catch (Exception e) {
            log.error("성적 저장 중 오류 발생", e);
            return "error: " + e.getMessage();
        }
    }

    /*================================테스트 페이지================================*/
    @GetMapping("/test")
    public String testIndex() {
        return "test-index";
    }

    /*================================교수 : 점수분배 목록/ 등록/ 수정================================*/
    // 점수 분배 목록 (교수가 개설한 모든 과목)
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADVISOR')")
    @GetMapping("/professor/system-list")
    public String listGradeSystem(@ModelAttribute PageRequestDTO req,
                                  @RequestParam(required = false) String searchType,
                                  @RequestParam(required = false) String searchKeyword,
                                  @AuthenticationPrincipal CustomUserDetails userDetails,
                                  Model model) {

        // 로그인한 사용자 정보에서 professorId 가져오기
        Long userId = userDetails.getUserId();
        Long professorId = gradeService.findProfessorIdByUserId(userId);

        // CourseService를 활용하여 교수가 개설한 강의 목록 조회
        PageResponseDTO<Map<String, Object>> coursesResult = 
                gradeService.listProfessorCoursesFromCourseService(professorId, searchType, searchKeyword, req);

        // 각 강의에 대한 GradeSystem 데이터 조회
        if (coursesResult != null && coursesResult.getData() != null) {
            for (Map<String, Object> course : coursesResult.getData()) {
                Long courseId = ((Number) course.get("COURSEID")).longValue();
                GradeSystem gradeSystem = gradeService.getGradeSystemByCourse(courseId);
                course.put("gradeSystem", gradeSystem);
            }
        }

        model.addAttribute("result", coursesResult);
        model.addAttribute("req", req);
        model.addAttribute("professorId", professorId);

        Map<String, Object> keep = new HashMap<>();
        keep.put("professorId", professorId);
        model.addAttribute("keepParams", keep);

        return "grade/professor/professor-system-list";
    }

    // 비율 수정 폼 (필요 시)
    @GetMapping("/professor/system/edit")
    public String professorSystemEditForm(@RequestParam Long courseId,
                                          @RequestParam(required = false) Long id,
                                          @RequestParam(required = false) Long subjectId,
                                          @AuthenticationPrincipal CustomUserDetails userDetails,
                                          Model model) {
        log.info("=== 점수 비율 수정 폼 ===");
        
        // 로그인한 사용자 정보에서 professorId 가져오기
        Long userId = userDetails.getUserId();
        Long professorId = gradeService.findProfessorIdByUserId(userId);
        
        log.info("professorId: {}, courseId: {}, id: {}, subjectId: {}", professorId, courseId, id, subjectId);
        
        GradeSystem gs;
        if (id != null) {
            // 기존 데이터 조회
            gs = gradeService.getGradeSystemById(id);
            log.info("기존 GradeSystem 조회됨: {}", gs);
        } else {
            // 새로 생성
            gs = new GradeSystem();
            gs.setCourseId(courseId);
            log.info("새 GradeSystem 생성됨");
        }
        
        // 과목명 조회 (기존 listProfessorCourses에서 가져온 데이터 활용)
        String subjectName = null;
        if (professorId != null) {
            PageRequestDTO req = new PageRequestDTO();
            PageResponseDTO<Map<String, Object>> coursesResult = 
                    gradeService.listProfessorCoursesFromCourseService(professorId, null, null, req);
            
            if (coursesResult != null && coursesResult.getData() != null) {
                for (Map<String, Object> course : coursesResult.getData()) {
                    Long courseIdFromList = ((Number) course.get("COURSEID")).longValue();
                    if (courseId.equals(courseIdFromList)) {
                        subjectName = (String) course.get("SUBJECTNAME");
                        break;
                    }
                }
            }
        }
        
        log.info("과목명 조회됨: {}", subjectName);
        log.info("GradeSystem 정보: {}", gs);
        
        model.addAttribute("gs", gs);
        model.addAttribute("subjectName", subjectName);
        model.addAttribute("professorId", professorId);
        model.addAttribute("courseId", courseId);
        model.addAttribute("subjectId", subjectId);

        // 현재 과목의 성적 규정(alphabet_system) 조회
        List<AlphabetSystem> subjectRules = gradeService.findAlphabetBySubject(courseId);
        List<AlphabetSystem> globalRules = gradeService.getDefaultAlphabetRules(); // 글로벌 규정 (기본값)

        model.addAttribute("subjectRules", subjectRules);
        model.addAttribute("globalRules", globalRules);
        
        return "grade/professor/professor-system-edit";
    }

    // 비율 저장 (추가/수정 통합)
    @PostMapping("/professor/system/edit")
    public String professorSystemEdit(@ModelAttribute GradeSystem gs,
                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("=== 점수 비율 저장 요청 ===");
        log.info("GradeSystem: {}", gs);
        
        // 로그인한 사용자 정보에서 professorId 가져오기
        Long userId = userDetails.getUserId();
        Long professorId = gradeService.findProfessorIdByUserId(userId);
        
        // 간단한 합계 검증: 100%
        double sum = (gs.getMidExamRatio() == null ? 0 : gs.getMidExamRatio())
                + (gs.getFinalExamRatio() == null ? 0 : gs.getFinalExamRatio())
                + (gs.getAssignmentRatio() == null ? 0 : gs.getAssignmentRatio())
                + (gs.getAttendanceRatio() == null ? 0 : gs.getAttendanceRatio());
        
        log.info("비율 합계: {} (검증: {})", sum, Math.round(sum) == 100);
        
        if (Math.round(sum) != 100) {
            throw new IllegalArgumentException("ratio sum must be 100");
        }
        
        if (gs.getId() == null) {
            log.info("새로운 GradeSystem 추가");
            gradeService.addGradeSystem(gs);
        } else {
            log.info("기존 GradeSystem 수정 (ID: {})", gs.getId());
            gradeService.editGradeSystem(gs);
        }
        
        log.info("저장 완료, 리다이렉트: /grade/professor/system-list");
        return "redirect:/grade/professor/system-list";
    }

    /*================================학생================================*/
    // 내 성적 목록 (수강신청 목록 포함)
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/student/{studentId}/list")
    public String listForStudent(@PathVariable Long studentId,
                                 @RequestParam(required = false) String searchType,
                                 @RequestParam(required = false) String searchKeyword,
                                 @ModelAttribute PageRequestDTO req,
                                 Model model) {

        PageResponseDTO<Map<String, Object>> result =
                gradeService.listMyEnrollmentsWithGrades(studentId, searchType, searchKeyword, req);

        model.addAttribute("result", result);
        model.addAttribute("req", req);
        model.addAttribute("studentId", studentId);

        Map<String, Object> keep = new HashMap<>();
        keep.put("studentId", studentId);
        if (searchType != null && !searchType.isBlank()) keep.put("searchType", searchType);
        if (searchKeyword != null && !searchKeyword.isBlank()) keep.put("searchKeyword", searchKeyword);
        model.addAttribute("keepParams", keep);

        return "grade/student/list";
    }

    // 학기별 성적 조회 (1~8학기)
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/student/semester-grades")
    public String listSemesterGrades(@AuthenticationPrincipal CustomUserDetails userDetails,
                                   Model model) {
        
        try {
            log.info("=== 학기별 성적 조회 시작 ===");
            
            // 로그인한 사용자 정보에서 studentId 가져오기
            Long userId = userDetails.getUserId();
            log.info("사용자 ID: {}", userId);
            
            Long studentId = gradeService.findStudentIdByUserId(userId);
            log.info("학생 ID: {}", studentId);
            
            if (studentId == null) {
                log.error("학생 ID를 찾을 수 없습니다. userId: {}", userId);
                model.addAttribute("error", "학생 정보를 찾을 수 없습니다.");
                return "error/error";
            }
            
            PageResponseDTO<Map<String, Object>> result = 
                    gradeService.listMyGradesBySemester(studentId);
            log.info("성적 조회 결과: {}개 학기", result.getData().size());

            // 다른 친구들 패턴 따라 UserService.findMyPageInfo() 사용
            MyPageInfoDTO studentInfo = userService.findMyPageInfo(userId, userDetails.getUser().getRole());
            log.info("학생 정보: {}", studentInfo);
            
            model.addAttribute("result", result);
            model.addAttribute("studentId", studentId);
            model.addAttribute("studentInfo", studentInfo);

            log.info("=== 학기별 성적 조회 완료 ===");
            return "grade/student/student-semester-grades";
            
        } catch (Exception e) {
            log.error("학기별 성적 조회 중 오류 발생", e);
            model.addAttribute("error", "성적 조회 중 오류가 발생했습니다: " + e.getMessage());
            return "error/error";
        }
    }

    // 학기별 상세 성적 조회 (특정 학기의 과목별 성적)
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/student/semester/{semesterId}/detail")
    public String listSemesterDetail(@PathVariable Long semesterId, 
                                    @AuthenticationPrincipal CustomUserDetails userDetails,
                                    Model model) {
        
        // 로그인한 사용자 정보에서 studentId 가져오기
        Long userId = userDetails.getUserId();
        Long studentId = gradeService.findStudentIdByUserId(userId);
        
        PageResponseDTO<Map<String, Object>> result = 
                gradeService.listMyGradesBySemesterDetail(studentId, semesterId);

        // 다른 친구들 패턴 따라 UserService.findMyPageInfo() 사용
        MyPageInfoDTO studentInfo = userService.findMyPageInfo(userId, userDetails.getUser().getRole());
        
        model.addAttribute("result", result);
        model.addAttribute("studentId", studentId);
        model.addAttribute("semesterId", semesterId);
        model.addAttribute("studentInfo", studentInfo);

        return "grade/student/student-semester-detail";
    }

    // 학생 성적 상세 보기
    @GetMapping("/student/{studentId}/detail/{id}")
    public String detailForStudent(@PathVariable Long studentId,
                                   @PathVariable Long id,
                                   Model model) {
        model.addAttribute("grade", gradeService.findGrade(id));
        model.addAttribute("studentId", studentId);
        return "grade/student/detail";
    }


}


