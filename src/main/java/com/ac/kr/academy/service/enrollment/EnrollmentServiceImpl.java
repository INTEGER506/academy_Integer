package com.ac.kr.academy.service.enrollment;

import com.ac.kr.academy.domain.course.Course;
import com.ac.kr.academy.domain.enrollment.Enrollment;
import com.ac.kr.academy.domain.subject.Subject;
import com.ac.kr.academy.dto.course.CourseDayTimeDTO;
import com.ac.kr.academy.dto.course.CourseListResponseDTO;
import com.ac.kr.academy.mapper.course.CourseMapper;
import com.ac.kr.academy.mapper.enrollment.EnrollmentMapper;
import com.ac.kr.academy.mapper.subject.SubjectMapper;
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
    private final SubjectMapper subjectMapper;

    @Override
    @Transactional
    public void enroll(Long courseId, Long studentId) {
        // 1. 중복 수강 신청 확인
        Optional<Enrollment> optional = enrollmentMapper.findByCourseIdAndStudentId(courseId, studentId);
        if (optional.isPresent()) {
            throw new IllegalArgumentException("이미 수강 신청한 강의입니다.");
        }

        // 2. 강의 상세 정보 및 현재 수강 인원 확인
        Course course = courseMapper.findCourseById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));

        int numOfStudent = Optional.ofNullable(course.getNumOfStudent()).orElse(0);
        int capacity = Optional.ofNullable(course.getCapacity()).orElse(0);

        if (numOfStudent >= capacity) {
            throw new IllegalStateException("해당 강의의 정원이 초과되었습니다.");
        }

        // 3. 수강 학점 초과 여부 (Subject 기준)
        Subject subject = subjectMapper.findById(course.getSubjectId());
        if (subject == null) {
            throw new IllegalArgumentException("해당 강좌의 과목을 찾을 수 없습니다. ID: " + course.getSubjectId());
        }

        int currentCredits = Optional.ofNullable(enrollmentMapper.findTotalCreditsByStudentId(studentId)).orElse(0);
        int newCourseCredit = subject.getCredit();

        if (currentCredits + newCourseCredit > 18) {
            throw new IllegalStateException("수강 가능 학점(18학점)을 초과했습니다.");
        }

        // 4. 시간표 중복 여부
        List<CourseDayTimeDTO> enrolledCourses = enrollmentMapper.findEnrolledCourseDayTimesByStudentId(studentId);
        boolean timeConflict = enrolledCourses.stream().anyMatch(ec ->
                Objects.equals(ec.getDayOfWeek(), course.getDayOfWeek()) &&
                        Objects.equals(ec.getTime(), course.getTime())
        );
        if (timeConflict) {
            throw new IllegalStateException("시간표가 겹치는 강의가 있습니다.");
        }

        // 5. 수강 신청 및 인원 업데이트
        Enrollment enrollment = new Enrollment();
        enrollment.setCourseId(courseId);
        enrollment.setStudentId(studentId);
        enrollmentMapper.save(enrollment);

        courseMapper.updateNumOfStudent(courseId, 1);
    }

    @Override
    @Transactional
    public void cancel(Long courseId, Long studentId) {
        // 수강 신청 내역 확인
        Enrollment enrollment = enrollmentMapper.findByCourseIdAndStudentId(courseId, studentId)
                .orElseThrow(() -> new IllegalArgumentException("수강 신청 내역이 존재하지 않습니다."));

        enrollmentMapper.deleteByCourseIdAndStudentId(courseId, studentId);
        courseMapper.updateNumOfStudent(courseId, -1);
    }

    @Override
    public List<CourseListResponseDTO> findMyCourses(Long studentId) {
        // Mapper에서 DTO로 바로 가져오기
        return enrollmentMapper.findMyCourses(studentId);
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
        return Optional.ofNullable(enrollmentMapper.findTotalCreditsByStudentId(studentId)).orElse(0);
    }

    @Override
    public List<Enrollment> findEnrollmentsByStudentId(Long studentId) {
        return enrollmentMapper.findByStudentId(studentId);
    }
}
