package com.ac.kr.academy.controller.enrollment;

import com.ac.kr.academy.dto.course.CourseListResponseDTO;
import com.ac.kr.academy.service.enrollment.EnrollmentService;
import com.ac.kr.academy.service.course.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final CourseService courseService;

    // 수강 신청 가능한 강의 목록 페이지
    @GetMapping
    public String courseList(Model model) {
        Long studentId = 1L; // 실제 로그인한 학생 ID 사용
        List<CourseListResponseDTO> courses = courseService.findAll(null, null); // 모든 강의 조회
        List<Long> enrolledCourseIds = enrollmentService.findMyCourses(studentId)
                .stream()
                .map(CourseListResponseDTO::getId)
                .collect(Collectors.toList());


        model.addAttribute("courseList", courses);
        model.addAttribute("enrolledCourseIds", enrolledCourseIds);

        return "enrollment/list";
    }

    // 강의 상세 페이지
    @GetMapping("/{courseId}")
    public String courseDetail(@PathVariable Long courseId, Model model) {
        CourseListResponseDTO course = courseService.findById(courseId);
        model.addAttribute("course", course);
        return "enrollment/detail";
    }

    // 내 수강 목록 페이지
    @GetMapping("/my-courses")
    public String myCourses(Model model) {
        Long studentId = 1L; // 실제 로그인한 학생 ID 사용
        List<CourseListResponseDTO> myCourses = enrollmentService.findMyCourses(studentId);
        model.addAttribute("myCourses", myCourses);
        return "enrollment/my-courses"; // /WEB-INF/views/enrollment/my-courses.jsp
    }

    // 수강 신청 처리 (POST)
    @PostMapping("/{courseId}")
    public String enroll(@PathVariable Long courseId,
                         RedirectAttributes redirectAttributes) {
        Long studentId = 1L;

        try {
            enrollmentService.enroll(courseId, studentId);
            redirectAttributes.addFlashAttribute("message", "수강 신청이 완료되었습니다.");
            return "redirect:/enrollments/my-courses";
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
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
