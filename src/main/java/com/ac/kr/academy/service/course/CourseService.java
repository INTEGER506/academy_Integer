package com.ac.kr.academy.service.course;


import com.ac.kr.academy.dto.course.CourseCreateRequestDTO;
import com.ac.kr.academy.dto.course.CourseListResponseDTO;

import java.util.List;

public interface CourseService {

    void addCourse(CourseCreateRequestDTO courseRequestDTO, Long userId);

    List<CourseListResponseDTO> findAll(String keyword, String type);

    CourseListResponseDTO findById(Long id);

    void update(CourseCreateRequestDTO courseRequestDTO, Long id, Long userId);

    void delete(Long id, Long userId);

}
