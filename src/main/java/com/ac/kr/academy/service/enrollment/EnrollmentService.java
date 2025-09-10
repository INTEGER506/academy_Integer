package com.ac.kr.academy.service.enrollment;


import com.ac.kr.academy.dto.course.CourseDayTimeDTO;

import java.util.List;

public interface EnrollmentService {

    void enroll(Long courseId, Long studentId);

    void cancel(Long courseId, Long studentId);

    List<CourseDayTimeDTO> findEnrolledCourseByStudentId(Long studentId);
}
