package com.ac.kr.academy.controller.grade;

import com.ac.kr.academy.service.grade.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/grade")
public class GradeController {

    private final GradeService gradeService;

    /*================================교수================================*/
    // 강의별 성적 목록 페이지
//    @GetMapping("")
//    public String courseGrades (@PathVariable Long courseId, Model model) {
//        model.addAttribute()
//    }
    /*================================학생================================*/

    /*================================공통================================*/
}
