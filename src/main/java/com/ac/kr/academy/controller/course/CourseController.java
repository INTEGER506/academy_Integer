package com.ac.kr.academy.controller.course;

import com.ac.kr.academy.dto.course.CourseCreateRequestDTO;
import com.ac.kr.academy.dto.course.CourseListResponseDTO;
import com.ac.kr.academy.dto.course.CourseUpdateRequestDTO;
import com.ac.kr.academy.service.course.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    // private final ProfessorService professorService;
    // private final SubjectService subjectService;
    // private final SemesterService semesterService;

    // 강의 목록 페이지
    @GetMapping
    public String courseList(Model model,
                             @RequestParam(name = "keyword", required = false) String keyword,
                             @RequestParam(name = "type", required = false) String type) {
        List<CourseListResponseDTO> courses = courseService.findAll(keyword, type);
        model.addAttribute("courses", courses);
        return "course/list";
    }

    // 강의 상세 페이지
    @GetMapping("/{id}")
    public String courseDetail(Model model, @PathVariable("id") Long id) {
        CourseListResponseDTO course = courseService.findById(id);
        model.addAttribute("course", course);
        return "course/detail";
    }

    // 강의 개설 폼 페이지
    @GetMapping("/add")
    public String createForm(Model model, @ModelAttribute("courseCreateRequestDTO") CourseCreateRequestDTO courseRequestDTO) {
        return "course/add";
    }

    // 강의 개설
    @PostMapping("/add")
    public String createCourse(@ModelAttribute("courseCreateRequestDTO") @Valid CourseCreateRequestDTO courseRequestDTO,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {

        if (bindingResult.hasErrors()) {
            return "course/add";
        }

        try {
            courseService.addCourse(courseRequestDTO, 1L); // user_id 임시로 1L
            redirectAttributes.addFlashAttribute("message", "강의가 성공적으로 개설되었습니다.");
            return "redirect:/courses";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/courses/add";
        }
    }

    // 강의 수정 폼 페이지
    @GetMapping("/edit/{id}")
    public String editForm(Model model, @PathVariable("id") Long id) {
        CourseUpdateRequestDTO course = courseService.findUpdateById(id);
        model.addAttribute("courseUpdateRequestDTO", course);
        return "course/edit";
    }

    // 강의 수정 요청을 처리
    @PostMapping("/edit/{id}")
    public String updateCourse(@PathVariable Long id,
                               @ModelAttribute("courseUpdateRequestDTO") @Valid CourseUpdateRequestDTO courseDTO,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "course/edit";
        }

        try {
            // userId는 임시로 1L로 설정
            courseService.update(courseDTO, id, 1L);
            redirectAttributes.addFlashAttribute("message", "강의 정보가 성공적으로 수정되었습니다.");
            return "redirect:/courses";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/courses/edit/" + id;
        }
    }


    // 강의 삭제
    @PostMapping("/delete/{id}")
    public String deleteCourse(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            courseService.delete(id, 1L); // user_id 임시로 1L
            redirectAttributes.addFlashAttribute("message", "강의가 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/courses";
    }

    // 강의 폐강
    @PostMapping("/close/{id}")
    public String closeCourse(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            courseService.closeCourse(id, 1L); // user_id 임시로 1L
            redirectAttributes.addFlashAttribute("message", "강의가 성공적으로 폐강되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/courses";
    }
}
