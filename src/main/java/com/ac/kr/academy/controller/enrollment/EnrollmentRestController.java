package com.ac.kr.academy.controller.enrollment;


import com.ac.kr.academy.service.enrollment.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;


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
            return new ResponseEntity<>(HttpStatus.CREATED); // 201 Created
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
    }

    // 수강 취소 처리 (DELETE) - RESTful API
    // 요청 URL: DELETE /api/enrollments/{courseId}?studentId={studentId}
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteEnrollment(
            @PathVariable("courseId") Long courseId,
            @RequestParam("studentId") Long studentId) {

        try {
            enrollmentService.cancel(courseId, studentId);
            return ResponseEntity.ok().build(); // 200 OK
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
}
