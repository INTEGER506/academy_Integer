package com.ac.kr.academy.controller.enrollment;


import com.ac.kr.academy.domain.enrollment.Enrollment;
import com.ac.kr.academy.dto.course.CourseDayTimeDTO;
import com.ac.kr.academy.service.enrollment.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentRestController {

    private final EnrollmentService enrollmentService;

    // 수강 신청
    @PostMapping("/{courseId}")
    public ResponseEntity<String> enroll(@PathVariable Long courseId) {

        Long studentId = 1L; // 로그인한 학생의 ID와 교환
        try {
            enrollmentService.enroll(courseId, studentId);
            return new ResponseEntity<>("수강 신청이 완료되었습니다.", HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new  ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 학생의 수강 신청 내역 조회
    @GetMapping("/my-courses")
    public ResponseEntity<List<CourseDayTimeDTO>> getMyEnrolledCourses() {
        Long studentId = 1L; // 로그인한 학생의 ID와 교환
        List<CourseDayTimeDTO> enrolledCourses = enrollmentService.findEnrolledCoursesByStudentId(studentId);
        return new  ResponseEntity<>(enrolledCourses, HttpStatus.OK);
    }

    // 해당 강의의 모든 수강 신청 내역 조회(교수/관리자용)
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<List<Enrollment>> getEnrollmentsByCourse(@PathVariable Long  courseId) {
        List<Enrollment> enrollments = enrollmentService.findEnrollmentsByCourseId(courseId);
        return new  ResponseEntity<>(enrollments, HttpStatus.OK);
    }

    // 모든 수강 신청 내역 조회(관리자용)
    @GetMapping
    public ResponseEntity<List<Enrollment>> getAllEnrollments() {
        List<Enrollment> allEnrollments = enrollmentService.findAllEnrollments();
        return new  ResponseEntity<>(allEnrollments, HttpStatus.OK);
    }

    // 수강 취소
    @DeleteMapping("/{courseId}")
    public ResponseEntity<String> cancel(@PathVariable Long courseId) {
        Long studentId = 1L; //로그인한 학생 ID와 교환
        try {
            enrollmentService.cancel(courseId, studentId);
            return new ResponseEntity<>("수강 취소가 완료되었습니다.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new  ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
