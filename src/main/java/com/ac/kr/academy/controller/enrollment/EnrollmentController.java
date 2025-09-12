package com.ac.kr.academy.controller.enrollment;


import com.ac.kr.academy.domain.enrollment.Enrollment;
import com.ac.kr.academy.service.enrollment.EnrollmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    // 모든 수강 신청 목록 페이지
    @GetMapping("/list")
    public String list(Model model){
        List<Enrollment> enrollments = enrollmentService.findAllEnrollments();
        model.addAttribute("enrollments", enrollments);
        return "enrollment/list";
    }

    // 수강 신청 폼 페이지
    @GetMapping("/new")
    public String enrollmentForm(Model model){
        model.addAttribute("enrollment", new Enrollment());
        return "enrollment/form";
    }

    // 수강 신청 처리
    @PostMapping
    public String create(@RequestParam Long courseId,
                         @RequestParam Long studentId, RedirectAttributes redirectAttributes){
        enrollmentService.enroll(courseId, studentId);
        redirectAttributes.addFlashAttribute("message", "수강 신청이 완료되었습니다.");
        return "redirect:/enrollments/list";
    }

    // 수강 신청 상세 페이지
    @GetMapping("/{id}")
    public String enrollmentDetails(@PathVariable Long id, Model model){
        Enrollment enrollment = enrollmentService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid enrollment ID:" + id));
        model.addAttribute("enrollment", enrollment);
        return "enrollment/details";
    }

    // 수정 폼 페이지
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model){
        Enrollment enrollment = enrollmentService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid enrollment ID:" + id));
        model.addAttribute("enrollment", enrollment);
        return "enrollment/edit";
    }

    //수강 신청 삭제 처리
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id,  RedirectAttributes redirectAttributes){
        Enrollment enrollment = enrollmentService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 수강 신청 내역이 존재하지 않습니다. ID: " + id));
        enrollmentService.cancel(enrollment.getCourseId(), enrollment.getStudentId());
        redirectAttributes.addFlashAttribute("message", "수강 신청이 취소되었습니다.");
        return "redirect:/enrollments/list";
    }
}
