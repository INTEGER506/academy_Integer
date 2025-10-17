package com.ac.kr.academy.controller.grade;

import com.ac.kr.academy.domain.course.Course;
import com.ac.kr.academy.domain.grade.AlphabetSystem;
import com.ac.kr.academy.domain.grade.Grade;
import com.ac.kr.academy.domain.grade.GradeSystem;
import com.ac.kr.academy.domain.subject.Subject;
import com.ac.kr.academy.dto.page.PageRequestDTO;
import com.ac.kr.academy.dto.page.PageResponseDTO;
import com.ac.kr.academy.service.grade.GradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/grade")
public class GradeController {

    private final GradeService gradeService;

    /*================================ 관리자 : 규정 목록/ CRUD ================================*/

    // 전체 규정 목록
    @GetMapping("/admin/rule/global")
    public String listAlphabetGlobal(@ModelAttribute PageRequestDTO req,
                                     @RequestParam(required = false) String searchType,
                                     @RequestParam(required = false) String searchKeyword,
                                     Model model) {

        PageResponseDTO<AlphabetSystem> result =
                gradeService.listAlphabetGlobal(searchType, searchKeyword, req);

        model.addAttribute("result", result);
        model.addAttribute("req", req);

        // 유지할 파라미터가 없으면 빈 Map
        model.addAttribute("keepParams", new HashMap<String, Object>());

        return "grade/admin/alphabet-list";
    }


    // 삭제 (POST)
    @PostMapping("/admin/alphabet/delete")
    public String adminAlphabetDelete(@RequestParam Long id) {
        gradeService.deleteAlphabetRule(id);
        return "redirect:/grade/admin/rule/global";
    }

    // 전체 규정 설정 폼
    @GetMapping("/admin/global-rules/form")
    public String globalRulesForm(Model model) {
        // 기존 글로벌 규정들을 Map으로 가져오기
        Map<String, Double> rules = gradeService.getGlobalRulesMap();
        model.addAttribute("rules", rules);
        return "grade/admin/global-rules-form";
    }

    // 전체 규정 저장은 RestController에서 처리 (/api/grade/admin/global-rules/save)


    

    // 과목별 규정 목록 (검색/페이징)
    @GetMapping("/admin/subject-rules/list")
    public String subjectRulesList(@RequestParam(required = false) String searchType,
                                   @RequestParam(required = false) String searchKeyword,
                                   @ModelAttribute PageRequestDTO req,
                                   Model model) {
        // 과목별 규정 상태 조회
        PageResponseDTO<Map<String, Object>> result = 
                gradeService.listSubjectRulesStatus(searchType, searchKeyword, req);
        
        // 글로벌 규정 조회
        Map<String, Double> globalRules = gradeService.getGlobalRulesMap();
        
        model.addAttribute("req", req);
        model.addAttribute("result", result);
        model.addAttribute("globalRules", globalRules);
        
        // 검색 파라미터 유지
        Map<String, Object> keep = new HashMap<>();
        if (searchType != null && !searchType.isBlank()) keep.put("searchType", searchType);
        if (searchKeyword != null && !searchKeyword.isBlank()) keep.put("searchKeyword", searchKeyword);
        model.addAttribute("keepParams", keep);
        
        return "grade/admin/subject-rules-list";
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

        // Alphabet 규정 조회 (글로벌)
        List<AlphabetSystem> globalRules = gradeService.getGlobalRulesAll();
        
        // Alphabet 규정 조회 (과목별)
        List<AlphabetSystem> subjectRules = gradeService.findAlphabetBySubject(courseId);
        
        // 점수 배율 조회
        GradeSystem gradeSystem = gradeService.getGradeSystemByCourse(courseId);

        model.addAttribute("result", result);
        model.addAttribute("req", req);
        model.addAttribute("professorId", professorId);
        model.addAttribute("globalRules", globalRules);
        model.addAttribute("subjectRules", subjectRules);
        model.addAttribute("gradeSystem", gradeSystem);

        Map<String, Object> keep = new HashMap<>();
        keep.put("professorId", professorId);
        if (courseId != null) keep.put("courseId", courseId);
        if (subjectId != null) keep.put("subjectId", subjectId);
        model.addAttribute("keepParams", keep);

        return "grade/professor/list";
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
    public String professorGradeAdd(@ModelAttribute Grade grade,
                                    @RequestParam(required = false) Long professorId,
                                    @RequestParam Long courseId,
                                    @RequestParam Long subjectId) {
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
    public String professorGradeEdit(@ModelAttribute Grade grade,
                                     @RequestParam(required = false) Long professorId,
                                     @RequestParam Long courseId,
                                     @RequestParam Long subjectId) {
        gradeService.editGrade(grade, professorId);
        return "redirect:/grade/professor/list?professorId=" + professorId
                + "&courseId=" + courseId + "&subjectId=" + subjectId;
    }

    // 삭제
    @PostMapping("/professor/delete")
    public String professorGradeDelete(@RequestParam Long id,
                                       @RequestParam(required = false) Long professorId,
                                       @RequestParam Long courseId,
                                       @RequestParam Long subjectId) {
        gradeService.deleteGrade(id, professorId);
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
    @GetMapping("/professor/rule/global")
    public String professorViewGlobalRules(Model model) {
        try {
            List<AlphabetSystem> globalRules = gradeService.getGlobalRulesAll();
            model.addAttribute("rules", globalRules);
            return "grade/professor/global-rules-view";
        } catch (Exception e) {
            model.addAttribute("error", "글로벌 규정을 불러오는 중 오류가 발생했습니다: " + e.getMessage());
            return "grade/professor/global-rules-view";
        }
    }

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
                                       @RequestParam Integer attendance) {
        try {
            Grade grade = new Grade();
            grade.setEnrollmentId(enrollmentId);
            grade.setMidExam(midExam);
            grade.setFinalExam(finalExam);
            grade.setAssignment(assignment);
            grade.setAttendance(attendance);
            
            gradeService.addGrade(grade, null);
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
    // 점수 분배 목록
    @GetMapping("/professor/system-list")
    public String listGradeSystem(@RequestParam Long courseId,
                                  @ModelAttribute PageRequestDTO req,
                                  @RequestParam(required = false) String searchType,
                                  @RequestParam(required = false) String searchKeyword,
                                  Model model) {

        PageResponseDTO<GradeSystem> result =
                gradeService.listGradeSystemByCourse(courseId, searchType, searchKeyword, req);

        model.addAttribute("result", result);
        model.addAttribute("req", req);
        model.addAttribute("courseId", courseId);

        // 과목 정보 추가 (subjectId를 위해)
        try {
            // courseId에서 subjectId를 찾는 로직이 필요하지만, 일단 courseId를 subjectId로 사용
            // 실제로는 Course 테이블에서 subjectId를 가져와야 함
            model.addAttribute("subjectId", courseId); // 임시로 courseId 사용
        } catch (Exception e) {
            model.addAttribute("subjectId", null);
        }

        Map<String, Object> keep = new HashMap<>();
        keep.put("courseId", courseId);
        model.addAttribute("keepParams", keep);

        return "grade/professor/system-list";
    }

    // 비율 수정 폼 (필요 시)
    @GetMapping("/professor/system/edit")
    public String professorSystemEditForm(@RequestParam Long courseId,
                                          @RequestParam(required = false) Long id,
                                          @RequestParam(required = false) Long subjectId,
                                          Model model) {
        GradeSystem gs = new GradeSystem();
        gs.setId(id);
        gs.setCourseId(courseId);
        model.addAttribute("gs", gs);
        
        // 글로벌 규정 정보 추가
        try {
            List<AlphabetSystem> globalRules = gradeService.getGlobalRulesAll();
            model.addAttribute("globalRules", globalRules);
        } catch (Exception e) {
            model.addAttribute("ruleError", "규정 정보를 불러오는 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return "grade/professor/system-edit";
    }

    // 비율 저장 (추가/수정 통합)
    @PostMapping("/professor/system/edit")
    public String professorSystemEdit(@ModelAttribute GradeSystem gs) {
        // 간단한 합계 검증: 100%
        double sum = (gs.getMidExamRatio() == null ? 0 : gs.getMidExamRatio())
                + (gs.getFinalExamRatio() == null ? 0 : gs.getFinalExamRatio())
                + (gs.getAssignmentRatio() == null ? 0 : gs.getAssignmentRatio())
                + (gs.getAttendanceRatio() == null ? 0 : gs.getAttendanceRatio());
        if (Math.round(sum) != 100) {
            throw new IllegalArgumentException("ratio sum must be 100");
        }
        if (gs.getId() == null) gradeService.addGradeSystem(gs);
        else gradeService.editGradeSystem(gs);
        return "redirect:/grade/professor/system-list?courseId=" + gs.getCourseId();
    }

    /*================================학생================================*/
    // 내 성적 목록
    @GetMapping("/student/{studentId}/list")
    public String listForStudent(@PathVariable Long studentId,
                                 @RequestParam(required = false) String searchType,
                                 @RequestParam(required = false) String searchKeyword,
                                 @ModelAttribute PageRequestDTO req,
                                 Model model) {

        PageResponseDTO<Grade> result =
                gradeService.listMyGrades(studentId, searchType, searchKeyword, req);

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


