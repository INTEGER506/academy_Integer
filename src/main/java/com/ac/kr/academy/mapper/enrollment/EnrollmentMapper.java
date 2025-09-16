package com.ac.kr.academy.mapper.enrollment;


import com.ac.kr.academy.domain.enrollment.Enrollment;
import com.ac.kr.academy.dto.course.CourseDayTimeDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Optional;

@Mapper
public interface EnrollmentMapper {

    // 수강 신청
    void save(Enrollment enrollment);

    // 수강 신청 내역 조회
    List<Enrollment> findByStudentId(Long studentId);

    // 강의 ID와 학생 ID로 수강 신청 내역 조회
    Optional<Enrollment> findByCourseIdAndStudentId(
            @Param("courseId") Long courseId,
            @Param("studentId") Long studentId);

    // 강의 ID로 수강 신청 인원 수 조회
    int countEnrollmentsByCourseId(Long courseId);

    // 학생 ID로 총 수강 학점 조회
    Integer findTotalCreditsByStudentId(Long studentId);

    // 학생의 수강 신청된 강의 시간표 조회
    List<CourseDayTimeDTO> findEnrolledCourseDayTimesByStudentId(Long studentId);

    // 강의 ID로 수강 신청 내역 조회
    List<Enrollment> findEnrollmentsByCourseId(Long courseId);

    // 모든 수강 신청 내역 조회
    List<Enrollment> findAllEnrollments();

    // ID로 단일 수강 신청 내역 조회
    Optional<Enrollment> findById(Long id);

    // 강의 ID와 학생 ID로 수강 신청 내역 삭제
    void deleteByCourseIdAndStudentId(
            @Param("courseId") Long courseId,
            @Param("studentId") Long studentId);

    // 강의 ID로 수강 신청 내역 삭제
    void deleteByCourseId(Long courseId);
}
