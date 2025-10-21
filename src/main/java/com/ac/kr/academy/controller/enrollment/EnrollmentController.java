package com.ac.kr.academy.controller.enrollment;

import com.ac.kr.academy.dto.course.CourseListResponseDTO;
import com.ac.kr.academy.dto.enrollment.EnrollmentListResponseDTO;
import com.ac.kr.academy.dto.page.PageRequestDTO;
import com.ac.kr.academy.dto.page.PageResponseDTO;
import com.ac.kr.academy.security.CustomUserDetails;
import com.ac.kr.academy.service.enrollment.EnrollmentService;
import com.ac.kr.academy.service.course.CourseService;
import com.ac.kr.academy.service.semester.SemesterService;
import com.ac.kr.academy.service.user.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/enrollments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final StudentService studentService;
    private final SemesterService semesterService;

    // 수강 신청 가능한 강의 목록 페이지
    @GetMapping
    public String availableCoursesPage(@ModelAttribute PageRequestDTO pageRequestDTO,
                                       @AuthenticationPrincipal CustomUserDetails userDetails,
                                       Model model) {

        Long userId = userDetails.getUserId();
        Long studentId = studentService.findStudentIdByUserId(userId);

        try {
            // 1. 현재 학기가 정상적으로 존재하는지 확인 (예외 발생 가능 지점)
            Long currentSemesterId = semesterService.getCurrentSemester()
                    .orElseThrow(() -> new IllegalStateException("현재 학기가 설정되어 있지 않아 강의 목록을 불러올 수 없습니다."))
                    .getId();

            // 2. 현재 학기가 정상적으로 조회된 경우에만 서비스 로직 실행
            PageResponseDTO<EnrollmentListResponseDTO> response =
                    enrollmentService.findAvailableCoursesPaged(pageRequestDTO, studentId, currentSemesterId);

            // 3. 학점 및 모델 추가
            int currentCredits = enrollmentService.findTotalCreditsByStudentId(studentId);
            int maxCredits = 18;

            model.addAttribute("courseList", response.getData());
            model.addAttribute("pageResponse", response);
            model.addAttribute("currentCredits", currentCredits);
            model.addAttribute("maxCredits", maxCredits);

        } catch (IllegalStateException e) {
            // 4. 예외 발생 시: 404 대신 오류 메시지, 빈 목록을 Model에 추가
            model.addAttribute("error", e.getMessage());
            model.addAttribute("courseList", java.util.Collections.emptyList());
            model.addAttribute("pageResponse", new PageResponseDTO<>());
        }

        return "enrollment/list";
    }


    // 내 수강 목록 페이지
    @GetMapping("/my-courses")
    public String myCourses(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        Long userId = userDetails.getUserId();
        Long studentId = studentService.findStudentIdByUserId(userId);

        // 내 수강 목록 조회
        List<EnrollmentListResponseDTO> myCoursesList =
                enrollmentService.findAllMyEnrolledCourses(studentId);

        // 현재 학점 및 최대 학점 계산
        int currentCredits = enrollmentService.findTotalCreditsByStudentId(studentId);
        int maxCredits = 18; // 필요 시 학과/학년별로 다르게 설정 가능

        model.addAttribute("myCourses", myCoursesList);
        model.addAttribute("currentCredits", currentCredits);
        model.addAttribute("maxCredits", maxCredits);

        return "enrollment/my-courses";
    }

    // 수강 신청
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/{courseId}")
    public String enrollCourse(@PathVariable Long courseId,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               RedirectAttributes redirectAttributes) {

        Long userId = userDetails.getUserId();
        Long studentId = studentService.findStudentIdByUserId(userId);

        try {
            enrollmentService.enroll(courseId, studentId);
            redirectAttributes.addFlashAttribute("message", "수강 신청이 완료되었습니다.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/enrollments";
    }

    // 수강 취소
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/cancel/{courseId}")
    public String cancelEnrollment(@PathVariable Long courseId,
                                   @AuthenticationPrincipal CustomUserDetails userDetails,
                                   RedirectAttributes redirectAttributes) {

        Long userId = userDetails.getUserId();
        Long studentId = studentService.findStudentIdByUserId(userId);

        try {
            enrollmentService.cancel(courseId, studentId);
            redirectAttributes.addFlashAttribute("message", "수강 신청이 취소되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/enrollments/my-courses";
    }
}


