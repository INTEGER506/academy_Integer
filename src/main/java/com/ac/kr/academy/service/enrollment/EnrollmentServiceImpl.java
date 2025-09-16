package com.ac.kr.academy.service.enrollment;

import com.ac.kr.academy.domain.course.Course;
import com.ac.kr.academy.domain.enrollment.Enrollment;
import com.ac.kr.academy.dto.course.CourseDayTimeDTO;
import com.ac.kr.academy.mapper.course.CourseMapper;
import com.ac.kr.academy.mapper.enrollment.EnrollmentMapper;
import com.ac.kr.academy.mapper.student.StudentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentMapper enrollmentMapper;
    private final CourseMapper courseMapper;
    private final StudentMapper studentMapper;

    @Override
    @Transactional
    public void enroll(Long courseId, Long studentId) {
        // 1️. 중복 수강 신청 확인
        if (enrollmentMapper.findByCourseIdAndStudentId(courseId, studentId).isPresent()) {
            throw new IllegalArgumentException("이미 수강 신청한 강의입니다.");
        }

        // 2️. 강의 상세 정보 및 현재 수강 인원 확인
        Course course = courseMapper.findCourseById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));

        if (course.getNumOfStudent() != null && course.getCapacity() != null &&
                course.getNumOfStudent() >= course.getCapacity()) {
            throw new IllegalStateException("해당 강의의 정원이 초과되었습니다.");
        }

        // 3️. 수강 학점 초과 여부
        int currentCredits = Optional.ofNullable(enrollmentMapper.findTotalCreditsByStudentId(studentId))
                .orElse(0);
        int newCourseCredit = Optional.ofNullable(courseMapper.findCreditByCourseId(courseId))
                .orElse(0);

        if (currentCredits + newCourseCredit > 18) {
            throw new IllegalStateException("수강 가능 학점(18학점)을 초과했습니다.");
        }

        // 4️. 시간표 중복 여부
        List<CourseDayTimeDTO> enrolledCourses = enrollmentMapper.findEnrolledCourseDayTimesByStudentId(studentId);
        boolean timeConflict = enrolledCourses.stream().anyMatch(ec ->
                Objects.equals(ec.getDayOfWeek(), course.getDayOfWeek()) &&
                        Objects.equals(ec.getTime(), course.getTime())
        );
        if (timeConflict) {
            throw new IllegalStateException("시간표가 겹치는 강의가 있습니다.");
        }

        // 5️. 수강 신청 및 인원 업데이트
        Enrollment enrollment = new Enrollment();
        enrollment.setCourseId(courseId);
        enrollment.setStudentId(studentId);
        enrollmentMapper.save(enrollment);

        courseMapper.updateNumOfStudent(courseId, 1);
    }

    @Override
    @Transactional
    public void cancel(Long courseId, Long studentId) {
        // 1.수강 신청 내역 존재 여부
        Enrollment enrollment = enrollmentMapper.findByCourseIdAndStudentId(courseId, studentId)
                .orElseThrow(() -> new IllegalArgumentException("수강 신청 내역이 존재하지 않습니다."));

        // 2.수강 신청 취소
        enrollmentMapper.deleteByCourseIdAndStudentId(courseId, studentId);

        // 3️.강의 정보 확인 및 수강 인원 감소
        Course course = courseMapper.findCourseById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));
        courseMapper.updateNumOfStudent(courseId, -1);
    }

    @Override
    public List<CourseDayTimeDTO> findEnrolledCourseDayTimesByStudentId(Long studentId) {
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

    @Override
    public int countEnrollmentsByCourseId(Long courseId) {
        return enrollmentMapper.countEnrollmentsByCourseId(courseId);
    }

    @Override
    public int findTotalCreditsByStudentId(Long studentId) {
        return Optional.ofNullable(enrollmentMapper.findTotalCreditsByStudentId(studentId))
                .orElse(0);
    }

    @Override
    public List<Enrollment> findEnrollmentsByStudentId(Long studentId) {
        return enrollmentMapper.findByStudentId(studentId);
    }
}
