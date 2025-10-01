package com.ac.kr.academy.controller.grade;

import com.ac.kr.academy.domain.grade.AlphabetSystem;
import com.ac.kr.academy.domain.grade.Grade;
import com.ac.kr.academy.domain.grade.GradeSystem;
import com.ac.kr.academy.dto.page.PageRequestDTO;
import com.ac.kr.academy.dto.page.PageResponseDTO;
import com.ac.kr.academy.service.grade.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/grade")
public class GradeController {

    private final GradeService gradeService;

    /*================================ 관리자 : 규정 목록/ CRUD ================================*/

    // 전체 규정 목록
    @GetMapping("/admin/rule/global")
    public String listAlphabetGlobal(@ModelAttribute PageRequestDTO req, Model model) {
        model.addAttribute("result", gradeService.listAlphabetGlobal(req));
        model.addAttribute("req", req);
        return "/grade/listAlphabetGlobal";
    }

    // 특정 과목 규정 목록
    @GetMapping("/admin/rule/subject")
    public String listAlphabetSubject(@RequestParam(required = false) Long subjectId,
                                      @RequestParam(required = false) Long enrollmentId,
                                      @ModelAttribute PageRequestDTO req,
                                      Model model) {
        model.addAttribute("result", gradeService.listAlphabetBySubject(subjectId, enrollmentId, req));
        model.addAttribute("subjectId", subjectId);
        model.addAttribute("enrollmentId", enrollmentId);
        model.addAttribute("req", req);
        return "/grade/admin/rule-subject";

    }

    // 전체 규정 동록
    @PostMapping("/admin/rule/global/add")
    public String addAlphabetGlobal(@ModelAttribute AlphabetSystem rule){
        // 전체: subject / enrollment Null
        rule.setSubjectId(null);
        rule.setEnrollmentId(null);
        gradeService.addAlphabetGlobal(rule);
        return "redirect:/grade/admin/rule/global";
    }

    // 규정 수정
    @PostMapping("/admin/rule/edit")
    public String editAlphabetRule(@ModelAttribute AlphabetSystem rule){
        gradeService.updateAlphabetRule(rule);
        return "redirect:/grade/admin/rule/global";
    }

    // 규정 삭제
    @PostMapping("/admin/rule/delete")
    public String deleteAlphabetRule(@RequestParam Long id){
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
                gradeService.listGradeSystemByCourse(professorId, courseId, subjectId, searchType, searchKeyword, req);

        model.addAttribute("result", result);
        model.addAttribute("req", req);
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
                                  Model model) {

        PageResponseDTO<GradeSystem> result =
                gradeService.listGradeSystemByCourse(courseId, req);

        model.addAttribute("result", result);
        model.addAttribute("courseId", courseId);
        model.addAttribute("req", req);
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
    @GetMapping("/student/list")
    public String listForStudent(@RequestParam Long studentId,
                                 @RequestParam(required = false) String searchType, // s = 과목명
                                 @RequestParam(required = false) String searchKeyword,
                                 @ModelAttribute PageRequestDTO req,
                                 Model model) {
        PageResponseDTO<Grade> result =
                gradeService.listMyGrades(studentId, searchType, searchKeyword, req);

        model.addAttribute("result", result);
        model.addAttribute("studentId", studentId);
        model.addAttribute("req", req);
        return "grade/student/list";
    }

    // 학생 성적 상세 보기
    @GetMapping("/student/detail")
    public String detailForStudent(@RequestParam Long studentId,
                                   @RequestParam Long id,
                                   Model model) {
        model.addAttribute("grade", gradeService.findGrade(id));
        return "grade/student/detail";
    }
}
