package com.ac.kr.academy.mapper.course;


import com.ac.kr.academy.domain.course.Course;
import com.ac.kr.academy.dto.course.CourseListResponseDTO;
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

    Optional<Course> findById(Long id);

    void update(Course course);

    void delete(Long id);

    boolean existsByDayOfWeekAndPlaceAndTime(@Param("dayOfWeek") String dayOfWeek,
                                             @Param("place") String place,
                                             @Param("time") String time);

    // 강의의 현재 수강 인원 수 업데이트
    void updateNumOfStudent(@Param("id") Long id,
                            @Param("numOfStudent")
                            int numOfStudent);
    // 특정 강의의 학점만 조회
    int findCreditByCourseId(Long courseId);


}
