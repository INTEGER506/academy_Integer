package com.ac.kr.academy.service.enrollment;


import com.ac.kr.academy.domain.course.Course;
import com.ac.kr.academy.domain.enrollment.Enrollment;
import com.ac.kr.academy.dto.course.CourseDayTimeDTO;
import com.ac.kr.academy.mapper.course.CourseMapper;
import com.ac.kr.academy.mapper.enrollment.EnrollmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentMapper enrollmentMapper;
    private final CourseMapper courseMapper;

    @Override
    @Transactional
    public void enroll(Long courseId, Long studentId) {
        // 중복 수강 신청 확인
        Optional<Enrollment> existingEnrollment = enrollmentMapper.findByCourseIdAndStudentId(courseId, studentId);
        if (existingEnrollment.isPresent()) {
            throw new IllegalArgumentException("이미 수강 신청한 강의입니다.");
        }

        // 강의 상세 정보 및 현재 수강 인원 확인
        Course course = courseMapper.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));

        if (course.getNumOfStudent() >= course.getCapacity()) {
            throw new IllegalStateException("해당 강의의 정원이 초과되었습니다.");
        }

        // 수강 학점 초과 여부
        int currentCredits = enrollmentMapper.findTotalCreditsByStudentId(studentId);
        int newCourseCredit = courseMapper.findCreditByCourseId(courseId);
        if (currentCredits + newCourseCredit > 18) {
            throw new IllegalStateException("수강 가능 학점(18학점)을 초과했습니다.");
        }

        // 시간표 중복 여부
        List<CourseDayTimeDTO> enrolledCourses = enrollmentMapper.findEnrolledCourseDayTimesByStudentId(studentId);
        for (CourseDayTimeDTO enrolledCourse : enrolledCourses) {
            if (enrolledCourse.getDayOfWeek().equals(course.getDayOfWeek()) &&
                    enrolledCourse.getTime().equals(course.getTime())) {
                throw new IllegalStateException("시간표가 겹치는 강의가 있습니다.");
            }
        }

        //모든 검증 통과 후, 수강 신청 및 인원 업데이트
        Enrollment enrollment = new Enrollment();
        enrollment.setCourseId(courseId);
        enrollment.setStudentId(studentId);
        enrollmentMapper.save(enrollment);

        //강의 현재 수강 인원 1 증가
        courseMapper.updateNumOfStudent(courseId,1);
    }

    @Override
    @Transactional
    public void cancel(Long courseId, Long studentId) {

        // 수강 신청 내역 존재 여부
        Optional<Enrollment> existingEnrollment = enrollmentMapper.findByCourseIdAndStudentId(courseId, studentId);
        if (existingEnrollment.isEmpty()) {
            throw new IllegalArgumentException("수강 신청 내역이 존재하지 않습니다.");
        }
        // 수강 신청 취소(삭제)
        enrollmentMapper.deleteByCourseIdAndStudentId(courseId, studentId);
        Course course = courseMapper.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));
        // 강의의 현재 수강 인원 1 감소
        courseMapper.updateNumOfStudent(courseId, - 1);
    }

    @Override
    public List<CourseDayTimeDTO> findEnrolledCoursesByStudentId(Long studentId) {
        return enrollmentMapper.findEnrolledCourseDayTimesByStudentId(studentId);
    }

    @Override
    public List<Enrollment> findEnrollmentsByCourseId(Long courseId) {
        return enrollmentMapper.findEnrollmentsByCourseId(courseId);
    }

    @Override
    public List<Enrollment> findAllEnrollments() {
        return enrollmentMapper.findAllEnrollments();
    }

    @Override
    public Optional<Enrollment> findById(Long id) {
        return enrollmentMapper.findById(id);
    }
}
