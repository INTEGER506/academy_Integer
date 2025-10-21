package com.ac.kr.academy.service.enrollment;

import com.ac.kr.academy.domain.enrollment.Enrollment;
import com.ac.kr.academy.dto.course.CourseDayTimeDTO;
import com.ac.kr.academy.dto.enrollment.EnrollmentListResponseDTO;
import com.ac.kr.academy.dto.page.PageRequestDTO;
import com.ac.kr.academy.dto.page.PageResponseDTO;

import java.util.List;
import java.util.Optional;

public interface EnrollmentService {

    // 수강 신청 가능 목록 조회(페이징 + 검색 + 신청 상태 포함)
    PageResponseDTO<EnrollmentListResponseDTO> findAvailableCoursesPaged(
            PageRequestDTO pageRequestDTO, Long studentId, Long currentSemesterId
    );

    // 학생 시간표 표시에 필요한 상세 정보를 조회
    List<CourseDayTimeDTO> findStudentTimeTable(Long studentId);

    // 수강 신청
    void enroll(Long courseId, Long studentId);

    // 수강 취소
    void cancel(Long courseId, Long studentId);

    // 학점 관련
    int findTotalCreditsByStudentId(Long studentId);

    // 특정 강의 수강 인원 수 (정원 제약 조건 체크 시 사용)
    int countEnrollmentsByCourseId(Long courseId);

    // 단건 조회
    Optional<Enrollment> findById(Long id);

    // 특정 강의의 전체 신청 기록 (관리자/교수용)
    List<Enrollment> findEnrollmentsByCourseId(Long courseId);

    // 특정 학생의 전체 신청 기록
    List<Enrollment> findEnrollmentsByStudentId(Long studentId);

    // 내 수강 목록 조회
    List<EnrollmentListResponseDTO> findAllMyEnrolledCourses(Long studentId);

    // 전체 수강 신청 기록 (관리자용)
    List<Enrollment> findAllEnrollments();
}
