package com.ac.kr.academy.mapper.enrollment;

import com.ac.kr.academy.domain.enrollment.Enrollment;
import com.ac.kr.academy.dto.course.CourseDayTimeDTO;
import com.ac.kr.academy.dto.enrollment.EnrollmentListResponseDTO;
import com.ac.kr.academy.dto.page.PageRequestDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Optional;

@Mapper
public interface EnrollmentMapper {

    // 수강 신청 가능 목록 전체 개수 (검색 조건 적용)
    long countAvailableCourses(@Param("pageRequestDTO") PageRequestDTO pageRequestDTO,
                               @Param("currentSemesterId") Long currentSemesterId);

    // 수강 신청 가능 목록 조회 (페이징 + 검색 + 신청 상태 포함)
    List<EnrollmentListResponseDTO> selectAvailableCoursesPaged(
            @Param("pageRequestDTO") PageRequestDTO pageRequestDTO,
            @Param("studentId") Long studentId,
            @Param("currentSemesterId") Long currentSemesterId);

    // 내 수강 목록 전체 개수 (검색 조건 적용)
    long countMyEnrolledCourses(
            @Param("pageRequestDTO") PageRequestDTO pageRequestDTO,
            @Param("studentId") Long studentId);

    // 내 수강 목록 조회 (페이징 + 검색 적용)
    List<EnrollmentListResponseDTO> selectMyEnrolledCoursesPaged(
            @Param("pageRequestDTO") PageRequestDTO pageRequestDTO,
            @Param("studentId") Long studentId);

    // 학생의 수강 신청된 강의 시간표 조회
    List<CourseDayTimeDTO> findStudentTimeTable(@Param("studentId") Long studentId,
                                                @Param("semesterId") Long currentSemesterId);

    // 수강 신청
    void save(Enrollment enrollment);

    // 강의 ID와 학생 ID로 수강 신청 내역 조회 (중복 확인 및 취소 시 사용)
    Optional<Enrollment> findByCourseIdAndStudentId(
            @Param("courseId") Long courseId,
            @Param("studentId") Long studentId);

    // 강의 ID로 수강 신청 인원 수 조회 (정원 체크)
    int countEnrollmentsByCourseId(Long courseId);

    // 학생 ID로 총 수강 학점 합산 조회 (학점 제한 체크)
    Integer findTotalCreditsByStudentId(Long studentId);

    // 강의 ID와 학생 ID로 수강 신청 내역 삭제
    int deleteByCourseIdAndStudentId(
            @Param("courseId") Long courseId,
            @Param("studentId") Long studentId);

    // 강의 ID로 수강 신청 내역 삭제
    void deleteByCourseId(Long courseId); // 강의 삭제 시 필요

    // 강의 ID로 수강 신청된 학생들의 ID 목록 조회
    List<Long> findStudentIdsByCourseId(@Param("courseId") Long courseId);

    // ID로 단일 수강 신청 내역 조회
    Optional<Enrollment> findById(Long id);

    // 강의 ID로 수강 신청 내역 조회
    List<Enrollment> findEnrollmentsByCourseId(Long courseId);

    // 학생 ID로 수강 신청 내역 조회
    List<Enrollment> findByStudentId(Long studentId);

    // 모든 수강 신청 내역 조회
    List<Enrollment> findAllEnrollments();

    // 내가 신청한 강의 목록 조회
    List<EnrollmentListResponseDTO> selectAllMyEnrolledCourses(Long studentId);
}
