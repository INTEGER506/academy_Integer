package com.ac.kr.academy.controller.enrollment;


import com.ac.kr.academy.dto.course.CourseListResponseDTO;
import com.ac.kr.academy.service.enrollment.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;


@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentRestController {

    private final EnrollmentService enrollmentService;


    // 수강 신청 처리 (POST) - RESTful API
    // 요청 URL: POST /api/enrollments/{courseId}?studentId={studentId}
    @PostMapping("/{courseId}")
    public ResponseEntity<Void> enroll(@PathVariable Long courseId,
                                       @RequestParam Long studentId) {
        try {
            enrollmentService.enroll(courseId, studentId);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Long courseId,
                                                 @RequestParam Long studentId) {
        try {
            enrollmentService.cancel(courseId, studentId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }


    // 내 수강 목록 조회 (GET)
    // 요청 URL: GET /api/enrollments/my-courses/{studentId}
    @GetMapping("/my-courses/{studentId}")
    public ResponseEntity<List<CourseListResponseDTO>> myCourses(@PathVariable Long studentId) {
        List<CourseListResponseDTO> myCourses = enrollmentService.findMyCourses(studentId);
        return ResponseEntity.ok(myCourses);
    }

}

