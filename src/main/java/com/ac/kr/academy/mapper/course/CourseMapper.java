package com.ac.kr.academy.mapper.course;


import com.ac.kr.academy.domain.course.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CourseMapper {

    void insert(Course course);

    List<Course> findAll(@Param("keyword") String keyword,
                         @Param("type") String type);

    Course findById(Long id);

    void update(Course course);

    void delete(Course course);
}
