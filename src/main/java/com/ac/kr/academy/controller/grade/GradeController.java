package com.ac.kr.academy.controller.grade;

import com.ac.kr.academy.domain.grade.AlphabetSystem;
import com.ac.kr.academy.domain.grade.Grade;
import com.ac.kr.academy.domain.grade.GradeSystem;
import com.ac.kr.academy.dto.page.PageRequestDTO;
import com.ac.kr.academy.dto.page.PageResponseDTO;
import com.ac.kr.academy.service.grade.GradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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

    // 추가 폼 (GET)
    @GetMapping("/admin/alphabet/add")
    public String adminAlphabetAddForm(Model model) {
        model.addAttribute("as", new AlphabetSystem());
        model.addAttribute("action", "/grade/admin/alphabet/add");
        return "grade/admin/alphabet-form";
    }

    // 추가 처리 (POST)
    @PostMapping("/admin/alphabet/add")
    public String adminAlphabetAdd(@ModelAttribute AlphabetSystem as) {
        gradeService.addAlphabetGlobal(as);
        return "redirect:/grade/admin/rule/global";
    }

    // 수정 폼 (GET) — 필요시 단건조회 붙이면 됨
    @GetMapping("/admin/alphabet/edit")
    public String adminAlphabetEditForm(@RequestParam Long id, Model model) {
        AlphabetSystem as = new AlphabetSystem();
        as.setId(id);
        model.addAttribute("as", as);
        model.addAttribute("action", "/grade/admin/alphabet/edit");
        return "grade/admin/alphabet-form";
    }

    // 수정 처리 (POST)
    @PostMapping("/admin/alphabet/edit")
    public String adminAlphabetEdit(@ModelAttribute AlphabetSystem as) {
        gradeService.updateAlphabetRule(as);
        return "redirect:/grade/admin/rule/global";
    }

    // 삭제 (POST)
    @PostMapping("/admin/alphabet/delete")
    public String adminAlphabetDelete(@RequestParam Long id) {
        gradeService.deleteAlphabetRule(id);
        return "redirect:/grade/admin/rule/global";
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

    // 추가 폼
    @GetMapping("/admin/rule/subject/add")
    public String adminRuleSubjectAddForm(@RequestParam(required = false) Long subjectId,
                                          @RequestParam(required = false) Long enrollmentId,
                                          Model model) {
        AlphabetSystem as = new AlphabetSystem();
        as.setSubjectId(subjectId);
        as.setEnrollmentId(enrollmentId);
        model.addAttribute("as", as);
        model.addAttribute("action", "/grade/admin/rule/subject/add");
        return "grade/admin/alphabet-form";
    }

    // 추가 처리
    @PostMapping("/admin/rule/subject/add")
    public String adminRuleSubjectAdd(@ModelAttribute AlphabetSystem as) {
        gradeService.addAlphabetRule(as);
        String redirect = "/grade/admin/rule/subject";
        redirect += (as.getSubjectId() != null ? "?subjectId=" + as.getSubjectId() : "");
        redirect += (as.getEnrollmentId() != null ? (redirect.contains("?") ? "&" : "?") + "enrollmentId=" + as.getEnrollmentId() : "");
        return "redirect:" + redirect;
    }

    // 수정 폼
    @GetMapping("/admin/rule/subject/edit")
    public String adminRuleSubjectEditForm(@RequestParam Long id,
                                           @RequestParam(required = false) Long subjectId,
                                           @RequestParam(required = false) Long enrollmentId,
                                           Model model) {
        AlphabetSystem as = new AlphabetSystem();
        as.setId(id);
        as.setSubjectId(subjectId);
        as.setEnrollmentId(enrollmentId);
        model.addAttribute("as", as);
        model.addAttribute("action", "/grade/admin/rule/subject/edit");
        return "grade/admin/alphabet-form";
    }

    // 수정 처리
    @PostMapping("/admin/rule/subject/edit")
    public String adminRuleSubjectEdit(@ModelAttribute AlphabetSystem as) {
        gradeService.updateAlphabetRule(as);
        String redirect = "/grade/admin/rule/subject";
        redirect += (as.getSubjectId() != null ? "?subjectId=" + as.getSubjectId() : "");
        redirect += (as.getEnrollmentId() != null ? (redirect.contains("?") ? "&" : "?") + "enrollmentId=" + as.getEnrollmentId() : "");
        return "redirect:" + redirect;
    }

    // 삭제
    @PostMapping("/admin/rule/subject/delete")
    public String adminRuleSubjectDelete(@RequestParam Long id,
                                         @RequestParam(required = false) Long subjectId,
                                         @RequestParam(required = false) Long enrollmentId) {
        gradeService.deleteAlphabetRule(id);
        String redirect = "/grade/admin/rule/subject";
        redirect += (subjectId != null ? "?subjectId=" + subjectId : "");
        redirect += (enrollmentId != null ? (redirect.contains("?") ? "&" : "?") + "enrollmentId=" + enrollmentId : "");
        return "redirect:" + redirect;
    }

    /*================================교수 : 성적 목록/CRUD================================*/
    // 성적 목록 (교수)
    @GetMapping("/professor/list")
    public String ListForProfessor(@RequestParam Long professorId,
                                   @RequestParam(required = false) Long courseId,
                                   @RequestParam(required = false) Long subjectId,
                                   @RequestParam(required = false) String searchType,
                                   @RequestParam(required = false) String searchKeyword,
                                   @ModelAttribute PageRequestDTO req,
                                   Model model
    ) {

        // 서비스 호출 -> 페이지 결과 수신
        PageResponseDTO<Grade> result =
                gradeService.listByCourse(professorId, courseId, subjectId, searchType, searchKeyword, req);

        model.addAttribute("result", result);
        model.addAttribute("req", req);
        model.addAttribute("professorId", professorId);

        Map<String, Object> keep = new HashMap<>();
        if (courseId != null) keep.put("courseId", courseId);
        if (subjectId != null) keep.put("subjectId", subjectId);
        model.addAttribute("keepParams", keep);

        return "grade/professor/list";
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

    // 등록 처리
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

        Map<String, Object> keep = new HashMap<>();
        keep.put("courseId", courseId);
        model.addAttribute("keepParams", keep);

        return "grade/professor/system-list";
    }

    // 비율 수정 폼 (필요 시)
    @GetMapping("/professor/system/edit")
    public String professorSystemEditForm(@RequestParam Long courseId,
                                          @RequestParam(required = false) Long id,
                                          Model model) {
        GradeSystem gs = new GradeSystem();
        gs.setId(id);
        gs.setCourseId(courseId);
        model.addAttribute("gs", gs);
        return "grade/professor/system-edit"; // 필요 시 생성(또는 modal로 처리)
    }

    // 비율 저장 (추가/수정 통합)
    @PostMapping("/professor/system/edit")
    public String professorSystemEdit(@ModelAttribute GradeSystem gs) {
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

    /*=======================테스트용=======================================*/

}


