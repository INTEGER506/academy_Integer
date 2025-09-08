package com.ac.kr.academy.service.course;


import com.ac.kr.academy.dto.course.CourseRequestDTO;
import com.ac.kr.academy.dto.course.CourseResponseDTO;

import java.util.List;

public interface CourseService {

    void addCourse(CourseRequestDTO courseRequestDTO, Long userId);

    List<CourseResponseDTO> findAll(String keyword, String type);

    CourseResponseDTO findById(Long id);

    void update(CourseRequestDTO courseRequestDTO,Long id, Long userId);

    void delete(Long id, Long userId);

}
