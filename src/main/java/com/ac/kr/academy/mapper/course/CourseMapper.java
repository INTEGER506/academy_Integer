package com.ac.kr.academy.mapper.course;

import com.ac.kr.academy.domain.course.Course;
import com.ac.kr.academy.dto.course.CourseListResponseDTO;
import com.ac.kr.academy.dto.course.CourseUpdateRequestDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Mapper
@Repository
public interface CourseMapper {

    // 강의 개설
    void insert(Course course);

    // 전체 강의 목록 조회
    List<CourseListResponseDTO> findAll(@Param("keyword") String keyword);

    // 과목명으로 강의 목록 조회
    List<CourseListResponseDTO> findAllBySubjectName(@Param("keyword") String keyword);

    // 학과명으로 강의 목록 조회
    List<CourseListResponseDTO> findAllByDeptName(@Param("keyword") String keyword);

    // 단일 강의 상세 조회
    Optional<CourseListResponseDTO> findById(Long id);

//    // 강의 정보 수정
//    void update(@Param("dto") CourseUpdateRequestDTO courseUpdateRequestDTO, @Param("id") Long id);
    // 강의 정보 수정
    void update(CourseUpdateRequestDTO courseUpdateRequestDTO, Long id);

    // 강의 삭제
    void delete(Long id);

    // 강의 폐강
    void closeCourse(Long id);

//    // 강의실 중복 확인
//    int existsByDayOfWeekAndPlaceAndTime(
//            @Param("dayOfWeek") String dayOfWeek,
//            @Param("place") String place,
//            @Param("time") String time);

    // 강의 학점 조회
    Optional<Integer> findCreditByCourseId(Long courseId);

    // 수강 인원 업데이트
    void updateNumOfStudent(@Param("courseId") Long courseId, @Param("change") int change);

    // 강의 상태 업데이트
    void updateStatus(@Param("courseId") Long courseId, @Param("status") String status);

    // 강의 정보(도메인) 조회
    Optional<Course> findCourseById(Long id);
}
