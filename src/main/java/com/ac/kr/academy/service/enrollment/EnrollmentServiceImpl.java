package com.ac.kr.academy.service.enrollment;


import com.ac.kr.academy.dto.course.CourseDayTimeDTO;
import com.ac.kr.academy.mapper.course.CourseMapper;
import com.ac.kr.academy.mapper.enrollment.EnrollmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentMapper enrollmentMapper;
    private final CourseMapper courseMapper;

    @Override
    @Transactional
    public void enroll(Long courseId, Long studentId) {

    }

    @Override
    public void cancel(Long courseId, Long studentId) {

    }

    @Override
    public List<CourseDayTimeDTO> findEnrolledCourseByStudentId(Long studentId) {
        return List.of();
    }
}
