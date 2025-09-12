package com.ac.kr.academy.mapper.enrollment;


import com.ac.kr.academy.domain.enrollment.Enrollment;
import com.ac.kr.academy.dto.course.CourseDayTimeDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface EnrollmentMapper {

    void save(Enrollment enrollment);

    Optional<Enrollment> findByCourseIdAndStudentId(@Param("courseId") Long courseId,
                                                    @Param("studentId") Long studentId);

    void deleteByCourseIdAndStudentId(@Param("courseId") Long  courseId, @Param("studentId") Long studentId);

    // 강의에 수강 신청한 학생 수 조회
    int countEnrollmentsByCourseId(Long courseId);

    int findTotalCreditsByStudentId(Long studentId);

    List<CourseDayTimeDTO> findEnrolledCourseDayTimesByStudentId(Long studentId);

    Optional<Enrollment> findById(Long id);

    List<Enrollment> findEnrollmentsByCourseId(Long courseId);

    List<Enrollment> findAllEnrollments();

    // 특정 강의의 모든 수강 신청 기록 삭제
    void deleteByCourseId(Long courseId);
}
