package com.ac.kr.academy.controller.course;

import com.ac.kr.academy.dto.course.CourseCreateRequestDTO;
import com.ac.kr.academy.dto.course.CourseListResponseDTO;
import com.ac.kr.academy.dto.course.CourseUpdateRequestDTO;
import com.ac.kr.academy.dto.page.PageRequestDTO;
import com.ac.kr.academy.dto.page.PageResponseDTO;
import com.ac.kr.academy.security.CustomUserDetails;
import com.ac.kr.academy.service.course.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /**
     * 강의 목록 페이지 및 폐강 후보 목록 조회 페이지
     */
    @GetMapping
    public String courseList(Model model,
                             @ModelAttribute PageRequestDTO pageRequestDTO,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 로그인 사용자 정보
        Long userId = userDetails.getUser().getId();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isProfessor = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSOR"));

        // 폐강 후보 목록 요청 여부
        boolean isCandidateRequest = "candidates".equals(pageRequestDTO.getSearchType());
        if (isCandidateRequest && !isAdmin) {
            throw new AccessDeniedException("폐강 후보 목록에 접근할 권한이 없습니다.");
        }

        // 교수인 경우 본인 강의만 조회
        Long filterProfessorId = null;
        if (isProfessor && !isAdmin) {
            filterProfessorId = courseService.findProfessorIdByUserId(userId);
        }

        // 페이징 처리된 강의 목록 조회
        PageResponseDTO<CourseListResponseDTO> pageResult =
                courseService.findAllPaged(pageRequestDTO, filterProfessorId);

        model.addAttribute("pageResult", pageResult);
        model.addAttribute("requestDTO", pageRequestDTO);

        return "course/list";
    }

    /**
     * 강의 개설 폼 페이지
     */
    @PreAuthorize("hasRole('PROFESSOR')")
    @GetMapping("/add")
    public String createForm(Model model,
                             @ModelAttribute("courseCreateRequestDTO") CourseCreateRequestDTO courseRequestDTO) {
        return "course/add";
    }

    /**
     * 강의 수정 폼 페이지
     */
    @PreAuthorize("hasAnyRole('PROFESSOR')")
    @GetMapping("/edit/{id}")
    public String editForm(Model model, @PathVariable("id") Long id) {
        CourseUpdateRequestDTO course = courseService.findUpdateById(id);
        model.addAttribute("courseUpdateRequestDTO", course);
        return "course/edit";
    }

    /**
     * 강의 수정 처리 (HiddenHttpMethodFilter를 통해 PATCH 요청으로 처리)
     * URL: /courses/{id} (메서드: PATCH)
     */
    @PreAuthorize("hasAnyRole('PROFESSOR')")
    @PostMapping("/edit/{id}")
    public String updateCourse(@PathVariable("id") Long id,
                               @ModelAttribute CourseUpdateRequestDTO courseUpdateRequestDTO,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               RedirectAttributes redirectAttributes) {

        Long userId = userDetails.getUserId();
        courseService.update(courseUpdateRequestDTO, id, userId);
        redirectAttributes.addFlashAttribute("message", "강의가 성공적으로 수정되었습니다.");
        return "redirect:/courses";
    }

    /**
     * 강의 삭제 처리 (HiddenHttpMethodFilter를 통해 DELETE 요청으로 처리)
     * URL: /courses/{id} (메서드: DELETE)
     */
    @PreAuthorize("hasAnyRole('PROFESSOR')")
    @PostMapping("/delete/{id}")
    public String deleteCourse(@PathVariable("id") Long id,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               RedirectAttributes redirectAttributes) {

        Long userId = userDetails.getUserId();
        try {
            courseService.delete(id, userId);
            redirectAttributes.addFlashAttribute("message", "강의가 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "강의 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/courses";
    }
}
