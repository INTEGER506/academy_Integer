package com.ac.kr.academy.controller.enrollment;


import com.ac.kr.academy.domain.enrollment.Enrollment;
import com.ac.kr.academy.service.enrollment.EnrollmentService;
import com.ac.kr.academy.service.course.CourseService;
import com.ac.kr.academy.dto.course.CourseListResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final CourseService courseService;

    // 수강 신청 가능한 강의 목록 페이지
    @GetMapping
    public String courseList(Model model) {
        List<CourseListResponseDTO> courses = courseService.findAll(null, null); // 모든 강의 조회
        model.addAttribute("courses", courses);
        return "enrollment/list";
    }

    // 강의 상세 페이지 및 수강 신청
    @GetMapping("/{courseId}")
    public String courseDetail(@PathVariable Long courseId, Model model) {
        CourseListResponseDTO course = courseService.findById(courseId);
        model.addAttribute("course", course);
        return "enrollment/detail";
    }

    // 내 수강 목록 페이지
    @GetMapping("/my-courses")
    public String myCourses(Model model) {

        Long studentId = 1L;
        List<Enrollment> enrollments = enrollmentService.findEnrollmentsByStudentId(studentId);
        model.addAttribute("enrollments", enrollments);
        return "enrollment/my-courses";
    }

    // 수강 신청 처리 (POST)
    @PostMapping("/{courseId}")
    public String enroll(@PathVariable Long courseId,
                         RedirectAttributes redirectAttributes) {

        Long studentId = 1L;

        try {
            enrollmentService.enroll(courseId, studentId);
            redirectAttributes.addFlashAttribute("message", "수강 신청이 완료되었습니다.");
            // 수강 신청 목록 페이지로 리다이렉션
            return "redirect:/enrollments/my-courses";
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            // 오류 발생 시 강의 목록 페이지로 리다이렉션
            return "redirect:/enrollments";
        }
    }

    // 수강 취소 처리 (POST)
    @PostMapping("/cancel/{courseId}")
    public String cancel(@PathVariable Long courseId,
                         RedirectAttributes redirectAttributes) {

        Long studentId = 1L;

        try {
            enrollmentService.cancel(courseId, studentId);
            redirectAttributes.addFlashAttribute("message", "수강 취소가 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/enrollments/my-courses";
    }
}
