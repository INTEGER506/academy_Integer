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

        return "/grade/listAlphabetGlobal";
    }

    // 특정 과목 규정 목록
    @GetMapping("/admin/rule/subject")
    public String listAlphabetSubject(@RequestParam(required = false) Long subjectId,
                                      @RequestParam(required = false) Long enrollmentId,
                                      @ModelAttribute PageRequestDTO req,
                                      @RequestParam(required = false) String searchType,
                                      @RequestParam(required = false) String searchKeyword,
                                      Model model) {

        var result = gradeService.listAlphabetBySubject(
                subjectId, enrollmentId, searchType, searchKeyword, req   // ★ 누락된 2개 추가
        );

        model.addAttribute("result", result);
        model.addAttribute("req", req);

        // 페이지 이동 시 유지할 값
        Map<String, Object> keep = new HashMap<>();
        if (subjectId != null) keep.put("subjectId", subjectId);
        if (enrollmentId != null) keep.put("enrollmentId", enrollmentId);
        model.addAttribute("keepParams", keep);

        return "/grade/admin/rule-subject";
    }

    // 전체 규정 동록
    @PostMapping("/admin/rule/global/add")
    public String addAlphabetGlobal(@ModelAttribute AlphabetSystem rule) {
        // 전체: subject / enrollment Null
        rule.setSubjectId(null);
        rule.setEnrollmentId(null);
        gradeService.addAlphabetGlobal(rule);
        return "redirect:/grade/admin/rule/global";
    }

    // 규정 수정
    @PostMapping("/admin/rule/edit")
    public String editAlphabetRule(@ModelAttribute AlphabetSystem rule) {
        gradeService.updateAlphabetRule(rule);
        return "redirect:/grade/admin/rule/global";
    }

    // 규정 삭제
    @PostMapping("/admin/rule/delete")
    public String deleteAlphabetRule(@RequestParam Long id) {
        gradeService.deleteAlphabetRule(id);
        return "redirect:/grade/admin/rule/global";
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
                gradeService.listByCourse(
                        professorId,                 // 교수
                        courseId,                    // 수업(선택)
                        subjectId,                   // 과목(선택)
                        req.getSearchType(),         // 검색 타입
                        req.getSearchKeyword(),      // 검색 키워드
                        req                          // 페이지/사이즈(start/end 포함)
                );

        model.addAttribute("result", result);
        model.addAttribute("req", req);

        // JSP에서 hidden으로 쓰게 모델 값 내려줌
        model.addAttribute("professorId", professorId);
        if (courseId != null) model.addAttribute("courseId", courseId);
        if (subjectId != null) model.addAttribute("subjectId", subjectId);

        Map<String, Object> keep = new HashMap<>();
        keep.put("professorId", professorId);
        if (courseId != null) keep.put("courseId", courseId);
        if (subjectId != null) keep.put("subjectId", subjectId);
        model.addAttribute("keepParams", keep);

        return "grade/professor/list";
    }

    // 성적 등록 폼
    @GetMapping("/professor/add")
    public String addForm(@RequestParam Long professorId,
                          @RequestParam Long enrollmentId,
                          Model model) {
        model.addAttribute("professorId", professorId);
        model.addAttribute("enrollmentId", enrollmentId);
        return "grade/professor/add";
    }

    // 성적 등록 처리
    @PostMapping("/professor/add")
    public String add(@RequestParam Long professorId,
                      @ModelAttribute Grade grade) {
        grade.setEnrollmentId(grade.getEnrollmentId());
        gradeService.addGrade(grade, professorId);
        return "redirect:/grade/professor/list?professorId=" + professorId;
    }

    // 성적 수정 폼
    @GetMapping("/professor/edit")
    public String editForm(@RequestParam Long id,
                           @RequestParam Long professorId,
                           Model model) {
        model.addAttribute("professorId", professorId);
        model.addAttribute("grade", gradeService.findGrade(id));
        return "grade/professor/edit";
    }

    // 성적 수정
    @PostMapping("/professor/edit")
    public String edit(@RequestParam Long professorId,
                       @ModelAttribute Grade grade) {
        gradeService.editGrade(grade, professorId);
        return "redirect:/grade/professor/list?professorId=" + professorId;
    }

    // 성적 삭제
    @PostMapping("/professor/delete")
    public String delete(@RequestParam Long id,
                         @RequestParam Long professorId) {
        gradeService.deleteGrade(id, professorId);
        return "redirect:/grade/professor/list?professorId=" + professorId;
    }

    /*================================교수 : 점수분배 목록/ 등록/ 수정================================*/
    // 점수 분배 목록
    @GetMapping("/professor/system/list")
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

        return "grade/professor/system/list";
    }

    // 점수분배 등록 처리
    @PostMapping("/professor/system/add")
    public String addGradeSystem(@RequestParam Long courseId,
                                 @RequestParam Long professorId,
                                 @ModelAttribute GradeSystem system) {
        system.setCourseId(courseId);
        gradeService.addGradeSystem(system, professorId);
        return "redirect:/grade/professor/system/list?courseId=" + courseId;
    }

    // 점수분배 수정 처리
    @PostMapping("/professor/system/edit")
    public String editGradeSystem(@RequestParam Long courseId,
                                  @RequestParam Long professorId,
                                  @ModelAttribute GradeSystem system) {
        system.setCourseId(courseId);
        gradeService.editGradeSystem(system, professorId);
        return "redirect:/grade/professor/system/list?courseId=" + courseId;
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


