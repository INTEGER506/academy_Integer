package com.ac.kr.academy.mapper.course;

import com.ac.kr.academy.domain.course.Course;
import com.ac.kr.academy.dto.course.CourseListResponseDTO;
import com.ac.kr.academy.dto.course.CourseCreateRequestDTO;
import com.ac.kr.academy.dto.course.CourseUpdateRequestDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CourseMapper {

    void insert(Course course);

    List<CourseListResponseDTO> findAll(@Param("keyword") String keyword);

    List<CourseListResponseDTO> findAllBySubjectName(@Param("keyword") String keyword);

    List<CourseListResponseDTO> findAllByDeptName(@Param("keyword") String keyword);

    Optional<CourseListResponseDTO> findById(Long id);

    void update(@Param("dto") CourseUpdateRequestDTO courseUpdateRequestDTO, @Param("id") Long id);

    void delete(Long id);

    void closeCourse(Long id);

    int existsByDayOfWeekAndPlaceAndTime(
            @Param("dayOfWeek") String dayOfWeek,
            @Param("place") String place,
            @Param("time") String time);

    Integer findCreditByCourseId(Long courseId);

    void updateNumOfStudent(@Param("courseId") Long courseId, @Param("change") int change);

    void updateStatus(@Param("courseId") Long courseId, @Param("status") String status);

    Optional<Course> findCourseById(Long id);  // Optional로 null 안전하게 처리
}
