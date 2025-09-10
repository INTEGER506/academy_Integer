package com.ac.kr.academy.mapper.course;


import com.ac.kr.academy.domain.course.Course;
import com.ac.kr.academy.dto.course.CourseListResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CourseMapper {

    void insert(Course course);

    List<CourseListResponseDTO> findAll(@Param("keyword") String keyword);

    List<CourseListResponseDTO> findAllBySubjectName(@Param("keyword") String keyword);

    List<CourseListResponseDTO> findAllByDeptName(@Param("keyword") String keyword);

    Course findById(Long id);

    void update(Course course);

    void delete(Long id);

    boolean existsByDayOfWeekAndPlaceAndTime(@Param("dayOfWeek") String dayOfWeek,
                                             @Param("place") String place,
                                             @Param("time") String time);


}
