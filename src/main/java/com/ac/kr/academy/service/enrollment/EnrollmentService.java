package com.ac.kr.academy.service.enrollment;


import com.ac.kr.academy.domain.enrollment.Enrollment;
import com.ac.kr.academy.dto.course.CourseDayTimeDTO;

import java.util.List;
import java.util.Optional;

public interface EnrollmentService {

    void enroll(Long courseId, Long studentId);

    void cancel(Long courseId, Long studentId);

    int countEnrollmentsByCourseId(Long courseId);

    int findTotalCreditsByStudentId(Long studentId);

    List<CourseDayTimeDTO> findEnrolledCourseDayTimesByStudentId(Long studentId);

    List<Enrollment> findEnrollmentsByCourseId(Long courseId);

    List<Enrollment> findAllEnrollments();

    Optional<Enrollment> findById(Long id);

}
