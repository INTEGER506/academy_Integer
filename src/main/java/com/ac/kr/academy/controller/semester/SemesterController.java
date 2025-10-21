package com.ac.kr.academy.controller.semester;

import com.ac.kr.academy.domain.semester.Semester;
import com.ac.kr.academy.service.semester.SemesterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/semester")
@PreAuthorize("hasRole('ADMIN')")
public class SemesterController {

    private final SemesterService semesterService;

    /** 관리자 화면 - 모든 학기 조회 */
    @GetMapping
    public String semesterPage(Model model) {
        List<Semester> semesters = semesterService.findAllSemesters();
        model.addAttribute("semesters", semesters);
        return "admin/semesterPage"; // JSP 파일명
    }

    /** 관리자 기능 - 학기 등록 */
    @PostMapping("/register")
    public String registerSemester(@ModelAttribute Semester semester) {
        semesterService.registerSemester(semester);
        return "redirect:/semester";
    }

    /** 관리자 기능 - 학기 수정 */
    @PostMapping("/update")
    public String updateSemester(@ModelAttribute Semester semester, RedirectAttributes redirectAttributes) {
        try {
            semesterService.updateSemester(semester);
            redirectAttributes.addFlashAttribute("message", "학기 정보 수정이 완료되었습니다.");
        } catch (Exception e) {

            redirectAttributes.addFlashAttribute("error", "학기 정보 수정 중 오류가 발생했습니다. (오류: " + e.getMessage() + ")");
        }
        return "redirect:/semester";
    }

    /** 관리자 기능 - 학기 삭제 */
    @PostMapping("/delete")
    public String deleteSemester(@RequestParam Long id) {
        semesterService.deleteSemester(id);
        return "redirect:/semester";
    }
}
