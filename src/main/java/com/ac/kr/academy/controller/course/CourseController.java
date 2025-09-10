package com.ac.kr.academy.controller.course;


import com.ac.kr.academy.dto.course.CourseCreateRequestDTO;
import com.ac.kr.academy.dto.course.CourseListResponseDTO;
import com.ac.kr.academy.dto.course.CourseUpdateRequestDTO;
import com.ac.kr.academy.service.course.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public String getCourseList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            Model model) {
        List<CourseListResponseDTO> courseList = courseService.findAll(keyword, type);
        model.addAttribute("courseList", courseList);
        return "course/List";
    }

    @GetMapping("/{id}")
    public String getCourseById(@PathVariable Long id, Model model) {
        CourseListResponseDTO course = courseService.findById(id);
        model.addAttribute("course", course);
        return "course/Detail";
    }

    @GetMapping("/create")
    public String createForm() {
        return "course/Create";
    }

    @PostMapping
    public String createCourse(@ModelAttribute @Valid CourseCreateRequestDTO courseRequestDTO,
                               @RequestParam Long userId, RedirectAttributes redirectAttributes) {
        courseService.addCourse(courseRequestDTO, userId);
        redirectAttributes.addFlashAttribute("message", "강의가 성공적으로 개설되었습니다.");
        return "redirect:/courses";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        CourseListResponseDTO course = courseService.findById(id);
        model.addAttribute("course", course);
        return "course/edit";
    }

    @PostMapping("/update/{id}")
    public String updateCourse(@PathVariable Long id,
                               @ModelAttribute @Valid CourseUpdateRequestDTO courseRequestDTO,
                               @RequestParam Long userId, RedirectAttributes redirectAttributes) {
        courseService.update(courseRequestDTO, id, userId);
        redirectAttributes.addFlashAttribute("message", "강의 정보가 성공적으로 수정되었습니다.");
        return "redirect:/courses/" + id;
    }

    @PostMapping("/delete/{id}")
    public String deleteCourse(@PathVariable Long id,
                               @RequestParam Long userId, RedirectAttributes redirectAttributes) {
        courseService.delete(id, userId);
        redirectAttributes.addFlashAttribute("message", "강의가 성공적으로 삭제되었습니다.");
        return "redirect:/courses";
    }
}
