package com.ac.kr.academy.controller.timetable;


import com.ac.kr.academy.domain.course.Course;
import com.ac.kr.academy.dto.course.CourseDayTimeDTO;
import com.ac.kr.academy.dto.course.CourseListResponseDTO;
import com.ac.kr.academy.mapper.user.professor.ProfessorMapper;
import com.ac.kr.academy.security.CustomUserDetails;
import com.ac.kr.academy.service.course.CourseService;
import com.ac.kr.academy.service.enrollment.EnrollmentService;
import com.ac.kr.academy.service.user.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/timetable")
public class TimetableController {

    private final EnrollmentService enrollmentService;
    private final CourseService courseService;
    private final ProfessorMapper professorMapper;
    private final StudentService studentService;

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public String studentTimetable(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        Long userId = userDetails.getUserId();
        Long studentId = studentService.findStudentIdByUserId(userId);

        List<CourseDayTimeDTO> courses = enrollmentService.findStudentTimeTable(studentId);
        model.addAttribute("courses", courses);
        return "timetable/student-timetable";
    }

    @GetMapping("/professor")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String professorTimetable( @AuthenticationPrincipal CustomUserDetails userDetails,
                                      Model model) {

        Long userId = userDetails.getUserId();
        Long professorId = professorMapper.findProfessorIdByUserId(userId);

        List<CourseDayTimeDTO> courses = courseService.findCoursesDayTimeByProfessorId(professorId);
        model.addAttribute("courses", courses);
        return "timetable/professor-timetable";
    }
}
