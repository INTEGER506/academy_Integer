package com.ac.kr.academy.service.course;


import com.ac.kr.academy.domain.dept.Dept;
import com.ac.kr.academy.dto.course.CourseCreateRequestDTO;
import com.ac.kr.academy.dto.course.CourseListResponseDTO;
import com.ac.kr.academy.mapper.course.CourseMapper;

import com.ac.kr.academy.mapper.subject.SubjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService{

    private final CourseMapper courseMapper;
    private final SubjectMapper subjectMapper;
    private final Dept

    @Override
    public void addCourse(CourseCreateRequestDTO courseRequestDTO, Long userId) {

    }

    @Override
    public List<CourseListResponseDTO> findAll(String keyword, String type) {
        return List.of();
    }

    @Override
    public CourseListResponseDTO findById(Long id) {
        return null;
    }

    @Override
    public void update(CourseCreateRequestDTO courseRequestDTO, Long id, Long userId) {

    }

    @Override
    public void delete(Long id, Long userId) {

    }
}
