package com.ac.kr.academy.controller.course;

import com.ac.kr.academy.dto.course.CourseCreateRequestDTO;
import com.ac.kr.academy.dto.course.CourseListResponseDTO;
import com.ac.kr.academy.dto.course.CourseUpdateRequestDTO;
import com.ac.kr.academy.service.course.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // 강의 개설 폼을 보여주는 GET 요청 처리
    @GetMapping("/add")
    public String addCourseForm(Model model) {
        model.addAttribute("courseCreateRequestDTO", new CourseCreateRequestDTO());
        return "course/add";
    }

    // 이 메서드가 POST /courses/add 요청을 처리합니다.
    @PostMapping("/add")
    public String addCourse(@Valid @ModelAttribute("courseCreateRequestDTO") CourseCreateRequestDTO course,
                            BindingResult bindingResult,
                            // 실제 환경에서는 SecurityContextHolder 등에서 userId를 가져와야 합니다.
                            Long professorId) {

        if (bindingResult.hasErrors()) {
            return "course/add";
        }

        courseService.addCourse(course, 1L); // userId는 임시로 1L로 설정했습니다.

        return "redirect:/courses";
    }
    @GetMapping
    public String listCourses(@RequestParam(value = "keyword", required = false) String keyword,
                              @RequestParam(value = "type", required = false) String type,
                              Model model) {
        List<CourseListResponseDTO> courses = courseService.findAll(keyword, type);
        model.addAttribute("courses", courses);
        return "course/list";
    }



//    // 강의 목록 페이지
//    @GetMapping
//    public String courseList(Model model,
//                             @RequestParam(name = "keyword", required = false) String keyword,
//                             @RequestParam(name = "type", required = false) String type) {
//        List<CourseListResponseDTO> courses = courseService.findAll(keyword, type);
//        model.addAttribute("courses", courses);
//        return "course/list";
//    }
//
//    // 강의 상세 페이지
//    @GetMapping("/{id}")
//    public String courseDetail(Model model, @PathVariable("id") Long id) {
//        CourseListResponseDTO course = courseService.findById(id);
//        model.addAttribute("course", course);
//        return "course/detail";
//    }
//
//    // 강의 개설 폼 페이지
//    @GetMapping("/add")
//    public String createForm(Model model, @ModelAttribute("course") CourseCreateRequestDTO courseRequestDTO) {
//        return "course/add";
//    }
//
//    // 강의 개설
//    @PostMapping
//    public String createCourse(@ModelAttribute("course") @Valid CourseCreateRequestDTO courseRequestDTO,
//                               BindingResult bindingResult,
//                               RedirectAttributes redirectAttributes) {
//
//        if (bindingResult.hasErrors()) {
//            return "course/add";
//        }
//
//        try {
//            courseService.addCourse(courseRequestDTO, 1L); // user_id 임시로 1L
//            redirectAttributes.addFlashAttribute("message", "강의가 성공적으로 개설되었습니다.");
//            return "redirect:/courses";
//        } catch (IllegalArgumentException e) {
//            redirectAttributes.addFlashAttribute("error", e.getMessage());
//            return "redirect:/courses/add";
//        }
//    }
//
//    // 강의 수정 폼 페이지
//    @GetMapping("/edit/{id}")
//    public String editForm(Model model, @PathVariable("id") Long id) {
//        CourseUpdateRequestDTO course = courseService.findUpdateById(id);
//        model.addAttribute("course", course);
//        return "course/edit";
//    }
//
//    // 강의 수정
//    @PostMapping("/edit/{id}")
//    public String editCourse(@ModelAttribute CourseUpdateRequestDTO courseUpdateRequestDTO,
//                             @PathVariable("id") Long id) {
//        courseService.update(courseUpdateRequestDTO, id, 1L); // user_id 임시로 1L
//        return "redirect:/courses/" + id;
//    }
//
//    // 강의 삭제
//    @PostMapping("/delete/{id}")
//    public String deleteCourse(@PathVariable("id") Long id) {
//        courseService.delete(id, 1L); // user_id 임시로 1L
//        return "redirect:/courses";
//    }
//
//    // 강의 폐강
//    @PostMapping("/close/{id}")
//    public String closeCourse(@PathVariable("id") Long id) {
//        courseService.closeCourse(id, 1L); // user_id 임시로 1L
//        return "redirect:/courses";
//    }
}
