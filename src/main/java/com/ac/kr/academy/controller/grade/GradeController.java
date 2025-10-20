package com.ac.kr.academy.controller.grade;

import com.ac.kr.academy.domain.course.Course;
import com.ac.kr.academy.domain.grade.AlphabetSystem;
import com.ac.kr.academy.domain.grade.Grade;
import com.ac.kr.academy.domain.grade.GradeSystem;
import com.ac.kr.academy.domain.subject.Subject;
import com.ac.kr.academy.dto.page.PageRequestDTO;
import com.ac.kr.academy.dto.page.PageResponseDTO;
import com.ac.kr.academy.service.grade.GradeService;
import com.ac.kr.academy.mapper.grade.GradeMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/grade")
public class GradeController {

    private final GradeService gradeService;
    private final GradeMapper gradeMapper;

    /*================================ 관리자 : 규정 목록/ CRUD ================================*/

    // 전체 규정 조회 및 수정
    @GetMapping("/admin/rule/global")
    public String adminGlobalRules(Model model) {
        log.info("=== 글로벌 규정 조회 시작 ===");
        
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
                rule.setDescription("상위 " + boundaries[i] + "%");
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
        log.info("=== 글로벌 규정 조회 완료 ===");
        return "grade/admin/alphabet-list";
    }

    // 삭제 (POST)
    @PostMapping("/admin/alphabet/delete")
    public String adminAlphabetDelete(@RequestParam Long id) {
        gradeService.deleteAlphabetRule(id);
        return "redirect:/grade/admin/rule/global";
    }
    
    // 글로벌 규정 초기화
    @PostMapping("/admin/rule/global/init")
    public String initGlobalRules() {
        log.info("=== 글로벌 규정 초기화 시작 ===");
        try {
            // 기존 글로벌 규정 삭제
            gradeService.getGradeMapper().deleteAlphabetGlobal();
            log.info("기존 글로벌 규정 삭제 완료");
            
            // 기본 상대평가 규정 생성
            String[] grades = {"A+", "A", "B+", "B", "C+", "C", "D"};
            double[] boundaries = {15.0, 30.0, 50.0, 70.0, 75.0, 80.0, 95.0};
            
            for (int i = 0; i < grades.length; i++) {
                AlphabetSystem rule = new AlphabetSystem();
                rule.setAlphabet(grades[i]);
                rule.setBoundary(boundaries[i]);
                rule.setCourseId(null); // 글로벌 규정
                rule.setDescription("상위 " + boundaries[i] + "%");
                gradeService.getGradeMapper().insertAlphabetGlobal(rule);
                log.info("글로벌 규정 생성: {} = {}%", grades[i], boundaries[i]);
            }
            
            log.info("=== 글로벌 규정 초기화 완료 ===");
            
            // 글로벌 규정 초기화 후 모든 과목에 자동 적용 (기존 메서드 활용)
            try {
                List<Subject> allSubjects = gradeService.getAllSubjects();
                log.info("총 과목 수: {}", allSubjects != null ? allSubjects.size() : 0);
                
                if (allSubjects != null && !allSubjects.isEmpty()) {
                    for (Subject subject : allSubjects) {
                        try {
                            gradeService.resetSubjectToGlobal(subject.getId());
                            log.info("과목 ID {}에 글로벌 규정 적용 완료", subject.getId());
                        } catch (Exception e) {
                            log.error("과목 ID {}에 글로벌 규정 적용 실패", subject.getId(), e);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("모든 과목에 글로벌 규정 적용 중 오류 발생", e);
            }
            
        } catch (Exception e) {
            log.error("글로벌 규정 초기화 실패", e);
        }
        
        return "redirect:/grade/admin/rule/global";
    }
    

    // 과목별 규정 목록 (검색/페이징)
    @GetMapping("/admin/subject-rules/list")
    public String subjectRulesList(@RequestParam(required = false) String searchType,
                                   @RequestParam(required = false) String searchKeyword,
                                   @ModelAttribute PageRequestDTO req,
                                   Model model) {
        log.info("=== 과목별 규정 목록 컨트롤러 시작 ===");
        log.info("요청 파라미터 - searchType: {}, searchKeyword: {}, req: {}", searchType, searchKeyword, req);
        
        // 과목별 규정 상태 조회
        PageResponseDTO<Map<String, Object>> result = 
                gradeService.listSubjectRulesStatus(searchType, searchKeyword, req);
        
        log.info("서비스에서 반환된 result: {}", result);
        log.info("result.data 크기: {}", result != null && result.getData() != null ? result.getData().size() : 0);
        
        model.addAttribute("req", req);
        model.addAttribute("result", result);
        
        // 실제 DB에서 글로벌 규정 조회 (subject-rules-list.jsp에서 사용)
        Map<String, Double> globalRules = new HashMap<>();
        List<AlphabetSystem> defaultRules = new ArrayList<>();
        
        try {
            // 실제 DB에서 글로벌 규정 조회
            List<AlphabetSystem> dbGlobalRules = gradeService.getGradeMapper().findGlobalAlphabetRules();
            log.info("DB에서 조회된 글로벌 규정 수: {}", dbGlobalRules != null ? dbGlobalRules.size() : 0);
            
            if (dbGlobalRules != null && !dbGlobalRules.isEmpty()) {
                // 실제 DB의 글로벌 규정 사용
                for (AlphabetSystem rule : dbGlobalRules) {
                    globalRules.put(rule.getAlphabet(), rule.getBoundary());
                    defaultRules.add(rule);
                    log.info("글로벌 규정: {} = {}%", rule.getAlphabet(), rule.getBoundary());
                }
            } else {
                // DB에 글로벌 규정이 없으면 기본값 사용
                log.warn("DB에 글로벌 규정이 없어서 기본값 사용");
                String[] grades = {"A+", "A", "B+", "B", "C+", "C", "D"};
                double[] boundaries = {15.0, 30.0, 50.0, 70.0, 75.0, 80.0, 95.0};
                
                for (int i = 0; i < grades.length; i++) {
                    AlphabetSystem rule = new AlphabetSystem();
                    rule.setAlphabet(grades[i]);
                    rule.setBoundary(boundaries[i]);
                    rule.setCourseId(null);
                    rule.setDescription("상위 " + boundaries[i] + "%");
                    defaultRules.add(rule);
                    globalRules.put(grades[i], boundaries[i]);
                }
            }
        } catch (Exception e) {
            log.error("글로벌 규정 조회 중 오류 발생", e);
            // 오류 발생 시 기본값 사용
            String[] grades = {"A+", "A", "B+", "B", "C+", "C", "D"};
            double[] boundaries = {15.0, 30.0, 50.0, 70.0, 75.0, 80.0, 95.0};
            
            for (int i = 0; i < grades.length; i++) {
                AlphabetSystem rule = new AlphabetSystem();
                rule.setAlphabet(grades[i]);
                rule.setBoundary(boundaries[i]);
                rule.setCourseId(null);
                rule.setDescription("상위 " + boundaries[i] + "%");
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
        
        return "grade/admin/subject-rules-list";
    }

    // 디버깅용: 데이터베이스 직접 확인
    @GetMapping("/admin/debug/subjects")
    @ResponseBody
    public String debugSubjects() {
        try {
            // 매퍼를 직접 호출해서 과목 데이터 확인
            List<Map<String, Object>> subjects = gradeService.getGradeMapper().findSubjectRulesStatus(null, null, 1, 10);
            
            StringBuilder result = new StringBuilder();
            result.append("=== 데이터베이스 과목 데이터 확인 ===\n");
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
                result.append("과목 데이터가 없습니다!\n");
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

    // 디버깅용: 교수 성적 목록 데이터 확인
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
                debug.append("데이터가 없습니다!\n");
            }
            
            return debug.toString();
        } catch (Exception e) {
            return "에러 발생: " + e.getMessage() + "\n" + e.getStackTrace()[0].toString();
        }
    }


    // 특정 과목 규정 목록
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

        return "grade/admin/alphabet-list";
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
    @GetMapping("/professor/courses")
    public String professorCourses(@RequestParam Long professorId,
                                   @RequestParam(required = false) String searchType,
                                   @RequestParam(required = false) String searchKeyword,
                                   @ModelAttribute PageRequestDTO req,
                                   Model model) {
        
        // 교수가 개설한 강의 목록 조회 (새로운 서비스 메서드 필요)
        PageResponseDTO<Map<String, Object>> result = 
                gradeService.listProfessorCourses(professorId, searchType, searchKeyword, req);

        model.addAttribute("result", result);
        model.addAttribute("req", req);
        model.addAttribute("professorId", professorId);

        Map<String, Object> keep = new HashMap<>();
        keep.put("professorId", professorId);
        model.addAttribute("keepParams", keep);

        return "grade/professor/courses";
    }

    // 성적 목록 (교수) - 2단계: 특정 강의의 학생 목록
    @GetMapping("/professor/list")
    public String ListForProfessor(@RequestParam Long professorId,
                                   @RequestParam(required = false) Long courseId,
                                   @RequestParam(required = false) Long subjectId,
                                   @RequestParam(required = false) String searchType,
                                   @RequestParam(required = false) String searchKeyword,
                                   @ModelAttribute PageRequestDTO req,
                                   Model model
    ) {
        
        // 디버그 로그 추가
        log.info("ListForProfessor 호출됨 - professorId: {}, courseId: {}, subjectId: {}", 
                 professorId, courseId, subjectId);

        // courseId가 없으면 강의 목록으로 리다이렉트
        if (courseId == null) {
            log.warn("courseId가 null입니다. 강의 목록으로 리다이렉트합니다.");
            return "redirect:/grade/professor/courses?professorId=" + professorId;
        }

        // 서비스 호출 -> 페이지 결과 수신
        PageResponseDTO<Grade> result =
                gradeService.listByCourse(professorId, courseId, subjectId, searchType, searchKeyword, req);
        
        // 디버깅 로그 추가
        log.info("교수 성적 목록 조회 결과 - 총 개수: {}", result.getTotalCount());
        if (result.getData() != null && !result.getData().isEmpty()) {
            for (Grade grade : result.getData()) {
                log.info("성적 데이터 - ID: {}, enrollmentId: {}, studentName: {}, studentNo: {}", 
                        grade.getId(), grade.getEnrollmentId(), grade.getStudentName(), grade.getStudentNo());
            }
        } else {
            log.warn("성적 데이터가 없습니다.");
        }

        // 🔥 수정: Alphabet 규정 조회 (과목별 커스텀 → 글로벌 순으로)
        List<AlphabetSystem> subjectRules = null;
        
        // 1단계: 과목별 커스텀 규정 확인
        if (subjectId != null) {
            subjectRules = gradeService.getGradeMapper().listAlphabetBySubjectAll(subjectId, null);
            log.info("과목별 커스텀 규정 조회 결과: {}개", subjectRules != null ? subjectRules.size() : 0);
        }
        
        // 2단계: 커스텀 규정이 없으면 글로벌 규정 사용
        if (subjectRules == null || subjectRules.isEmpty()) {
            subjectRules = gradeService.getGradeMapper().findGlobalAlphabetRules();
            log.info("글로벌 규정 조회 결과: {}개", subjectRules != null ? subjectRules.size() : 0);
        }
        
        // 🔥 추가: A+부터 순서대로 정렬 (A+, A, B+, B, C+, C, D 순서)
        if (subjectRules != null && !subjectRules.isEmpty()) {
            String[] gradeOrder = {"A+", "A", "B+", "B", "C+", "C", "D"};
            subjectRules.sort((a, b) -> {
                int indexA = java.util.Arrays.asList(gradeOrder).indexOf(a.getAlphabet());
                int indexB = java.util.Arrays.asList(gradeOrder).indexOf(b.getAlphabet());
                return Integer.compare(indexA, indexB);
            });
            log.info("규정 정렬 완료 - A+부터 순서대로");
        }
        
        // 점수 배율 조회
        GradeSystem gradeSystem = gradeService.getGradeSystemByCourse(courseId);

        model.addAttribute("result", result);
        model.addAttribute("req", req);
        model.addAttribute("professorId", professorId);
        model.addAttribute("subjectRules", subjectRules);
        model.addAttribute("gradeSystem", gradeSystem);

        Map<String, Object> keep = new HashMap<>();
        keep.put("professorId", professorId);
        if (courseId != null) keep.put("courseId", courseId);
        if (subjectId != null) keep.put("subjectId", subjectId);
        model.addAttribute("keepParams", keep);

        return "grade/professor/list";
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

    // 등록 처리 (폼 제출용)
    @PostMapping("/professor/add")
    public String professorGradeAdd(@RequestParam Long enrollmentId,
                                    @RequestParam Integer midExam,
                                    @RequestParam Integer finalExam,
                                    @RequestParam Integer assignment,
                                    @RequestParam Integer attendance,
                                    @RequestParam(required = false) Long professorId,
                                    @RequestParam Long courseId,
                                    @RequestParam Long subjectId) {
        
        Grade grade = new Grade();
        grade.setEnrollmentId(enrollmentId);
        grade.setMidExam(midExam);
        grade.setFinalExam(finalExam);
        grade.setAssignment(assignment);
        grade.setAttendance(attendance);
        
        gradeService.addGrade(grade, professorId);

        return "redirect:/grade/professor/list?professorId=" + professorId
                + "&courseId=" + courseId + "&subjectId=" + subjectId;
    }

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

    // 삭제
    @PostMapping("/professor/delete")
    public String professorGradeDelete(@RequestParam String id,
                                       @RequestParam(required = false) String professorId,
                                       @RequestParam String courseId,
                                       @RequestParam String subjectId) {
        
        log.info("성적 삭제 요청 - id: {}, professorId: {}, courseId: {}, subjectId: {}", 
                id, professorId, courseId, subjectId);
        
        try {
            // String을 Long으로 변환
            Long gradeId = Long.parseLong(id);
            Long profId = (professorId != null && !professorId.isEmpty()) ? Long.parseLong(professorId) : null;
            Long courseIdLong = Long.parseLong(courseId);
            Long subjectIdLong = Long.parseLong(subjectId);
            
            gradeService.deleteGrade(gradeId, profId);
            log.info("성적 삭제 완료 - id: {}", gradeId);
        } catch (NumberFormatException e) {
            log.error("숫자 변환 오류: {}", e.getMessage());
            return "redirect:/grade/professor/list?professorId=" + professorId
                    + "&courseId=" + courseId + "&subjectId=" + subjectId;
        } catch (Exception e) {
            log.error("성적 삭제 실패 - id: {}, error: {}", id, e.getMessage());
        }
        
        return "redirect:/grade/professor/list?professorId=" + professorId
                + "&courseId=" + courseId + "&subjectId=" + subjectId;
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
    @GetMapping("/professor/system-list")
    public String listGradeSystem(@RequestParam Long professorId,
                                  @ModelAttribute PageRequestDTO req,
                                  @RequestParam(required = false) String searchType,
                                  @RequestParam(required = false) String searchKeyword,
                                  Model model) {

        // 교수가 개설한 강의 목록 조회
        PageResponseDTO<Map<String, Object>> coursesResult = 
                gradeService.listProfessorCourses(professorId, searchType, searchKeyword, req);

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

        return "grade/professor/system-list";
    }

    // 비율 수정 폼 (필요 시)
    @GetMapping("/professor/system/edit")
    public String professorSystemEditForm(@RequestParam Long professorId,
                                          @RequestParam Long courseId,
                                          @RequestParam(required = false) Long id,
                                          @RequestParam(required = false) Long subjectId,
                                          Model model) {
        log.info("=== 점수 비율 수정 폼 ===");
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
                    gradeService.listProfessorCourses(professorId, null, null, req);
            
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
        
        return "grade/professor/system-edit";
    }

    // 비율 저장 (추가/수정 통합)
    @PostMapping("/professor/system/edit")
    public String professorSystemEdit(@ModelAttribute GradeSystem gs,
                                      @RequestParam Long professorId) {
        log.info("=== 점수 비율 저장 요청 ===");
        log.info("GradeSystem: {}", gs);
        
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
        
        log.info("저장 완료, 리다이렉트: /grade/professor/system-list?professorId={}", professorId);
        return "redirect:/grade/professor/system-list?professorId=" + professorId;
    }

    /*================================학생================================*/
    // 내 성적 목록 (수강신청 목록 포함)
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
    @GetMapping("/student/{studentId}/semester-grades")
    public String listSemesterGrades(@PathVariable Long studentId, 
                                   HttpSession session, 
                                   Model model) {
        
        // 세션에서 현재 로그인한 학생 ID 확인
        Long sessionStudentId = (Long) session.getAttribute("studentId");
        log.info("URL studentId: {}, 세션 studentId: {}", studentId, sessionStudentId);
        
        // 세션의 학생 ID가 있으면 그것을 사용, 없으면 URL의 studentId 사용
        Long actualStudentId = (sessionStudentId != null) ? sessionStudentId : studentId;
        log.info("실제 사용할 studentId: {}", actualStudentId);
        
        PageResponseDTO<Map<String, Object>> result = 
                gradeService.listMyGradesBySemester(actualStudentId);

        // 실제 학생 정보 조회 (데이터베이스에서)
        log.info("🎯 getStudentInfoFromDB 호출 시작 - actualStudentId: {}", actualStudentId);
        Map<String, Object> studentInfo = getStudentInfoFromDB(actualStudentId);
        log.info("🎯 getStudentInfoFromDB 호출 완료 - studentInfo: {}", studentInfo);
        
        model.addAttribute("result", result);
        model.addAttribute("studentId", actualStudentId);
        model.addAttribute("studentInfo", studentInfo);

        return "grade/student/semester-grades";
    }

    // 학기별 상세 성적 조회 (특정 학기의 과목별 성적)
    @GetMapping("/student/{studentId}/semester/{semesterId}/detail")
    public String listSemesterDetail(@PathVariable Long studentId, 
                                    @PathVariable Long semesterId, 
                                    HttpSession session,
                                    Model model) {
        
        log.info("=== 학기별 상세 성적 조회 컨트롤러 시작 ===");
        log.info("URL studentId: {}, semesterId: {}", studentId, semesterId);
        
        // 세션에서 현재 로그인한 학생 ID 확인 (여러 가능한 키 체크)
        Long sessionStudentId = (Long) session.getAttribute("studentId");
        Long userId = (Long) session.getAttribute("userId");
        Long studentIdFromSession = (Long) session.getAttribute("student");
        
        log.info("세션 studentId: {}", sessionStudentId);
        log.info("세션 userId: {}", userId);
        log.info("세션 student: {}", studentIdFromSession);
        
        // 세션의 모든 속성 확인 (디버깅용)
        log.info("세션 전체 속성:");
        session.getAttributeNames().asIterator().forEachRemaining(name -> 
            log.info("  - {}: {}", name, session.getAttribute(name))
        );
        
        // 실제 사용할 studentId 결정 (우선순위: sessionStudentId > userId > URL studentId)
        Long actualStudentId = studentId; // 기본값은 URL의 studentId
        
        if (sessionStudentId != null) {
            actualStudentId = sessionStudentId;
            log.info("세션 studentId 사용: {}", actualStudentId);
        } else if (userId != null) {
            actualStudentId = userId;
            log.info("세션 userId 사용: {}", actualStudentId);
        } else {
            log.info("URL studentId 사용: {}", actualStudentId);
        }
        
        log.info("최종 사용할 studentId: {}", actualStudentId);
        
        // 학생 정보 확인 (학번 불일치 문제 해결)
        try {
            // 학생 정보 조회하여 로그 출력
            log.info("학생 ID {}에 대한 성적 조회 시도", actualStudentId);
        } catch (Exception e) {
            log.error("학생 정보 조회 중 오류 발생: {}", e.getMessage());
        }
        
        PageResponseDTO<Map<String, Object>> result = 
                gradeService.listMyGradesBySemesterDetail(actualStudentId, semesterId);

        log.info("컨트롤러에서 받은 result: {}", result);
        log.info("result.data 크기: {}", result.getData() != null ? result.getData().size() : 0);
        
        if (result.getData() != null && !result.getData().isEmpty()) {
            log.info("=== 컨트롤러에서 전달할 데이터 ===");
            for (int i = 0; i < result.getData().size(); i++) {
                Map<String, Object> subject = result.getData().get(i);
                log.info("과목 {}: {}", i + 1, subject);
            }
            log.info("=== 컨트롤러에서 전달할 데이터 끝 ===");
        } else {
            log.warn("⚠️ 성적 데이터가 없습니다! studentId: {}, semesterId: {}", studentId, semesterId);
            log.warn("학번 불일치 문제일 수 있습니다. 실제 학생 ID를 확인해주세요.");
        }

        // 실제 학생 정보 조회 (데이터베이스에서)
        Map<String, Object> studentInfo = getStudentInfoFromDB(actualStudentId);
        
        model.addAttribute("result", result);
        model.addAttribute("studentId", actualStudentId);
        model.addAttribute("semesterId", semesterId);
        model.addAttribute("studentInfo", studentInfo);

        log.info("=== 학기별 상세 성적 조회 컨트롤러 완료 ===");
        return "grade/student/semester-detail";
    }
    
    // GradeMapper를 활용한 학생 정보 조회 (user 관련 파일 수정 없이)
    private Map<String, Object> getStudentInfoFromDB(Long studentId) {
        log.info("🔍 학생 정보 조회 시작 - studentId: {}", studentId);
        
        try {
            // GradeMapper를 통해 직접 학생 정보 조회
            log.info("📞 GradeMapper.findStudentInfoById({}) 호출", studentId);
            Map<String, Object> directStudentInfo = gradeMapper.findStudentInfoById(studentId);
            log.info("📋 직접 조회 결과: {}", directStudentInfo);
            log.info("📋 직접 조회 결과 타입: {}", directStudentInfo != null ? directStudentInfo.getClass() : "null");
            log.info("📋 직접 조회 결과 크기: {}", directStudentInfo != null ? directStudentInfo.size() : "null");
            
            if (directStudentInfo != null && !directStudentInfo.isEmpty()) {
                // 직접 조회로 성공한 경우
                Map<String, Object> studentInfo = new HashMap<>();
                studentInfo.put("studentId", directStudentInfo.get("studentId"));
                studentInfo.put("studentName", directStudentInfo.get("studentName"));
                studentInfo.put("studentNo", directStudentInfo.get("studentNo"));
                studentInfo.put("email", directStudentInfo.get("email"));
                studentInfo.put("phone", directStudentInfo.get("phone"));
                studentInfo.put("major", directStudentInfo.get("major") != null ? directStudentInfo.get("major") : "컴퓨터공학과");
                studentInfo.put("admissionYear", directStudentInfo.get("admissionYear") != null ? directStudentInfo.get("admissionYear") : "2025");
                log.info("✅ GradeMapper로 학생 정보 조회 성공: {}", studentInfo);
                return studentInfo;
            } else {
                log.warn("❌ GradeMapper로 학생 정보를 찾을 수 없습니다 - studentId: {}", studentId);
                log.warn("❌ directStudentInfo가 null이거나 비어있음");
                // 기본값 설정
                Map<String, Object> studentInfo = new HashMap<>();
                studentInfo.put("studentId", studentId);
                studentInfo.put("studentName", "학생정보없음");
                studentInfo.put("studentNo", "-");
                studentInfo.put("email", "-");
                studentInfo.put("phone", "-");
                studentInfo.put("major", "-");
                studentInfo.put("admissionYear", "-");
                return studentInfo;
            }
        } catch (Exception e) {
            log.error("💥 학생 정보 조회 중 오류 발생 - studentId: {}", studentId, e);
            // 기본값 설정
            Map<String, Object> studentInfo = new HashMap<>();
            studentInfo.put("studentId", studentId);
            studentInfo.put("studentName", "학생정보없음");
            studentInfo.put("studentNo", "-");
            studentInfo.put("email", "-");
            studentInfo.put("phone", "-");
            studentInfo.put("major", "-");
            studentInfo.put("admissionYear", "-");
            return studentInfo;
        }
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


