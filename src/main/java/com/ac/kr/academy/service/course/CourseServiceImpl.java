package com.ac.kr.academy.service.course;

import com.ac.kr.academy.domain.subject.Subject;
import com.ac.kr.academy.dto.course.CourseCreateRequestDTO;
import com.ac.kr.academy.dto.course.CourseListResponseDTO;
import com.ac.kr.academy.dto.course.CourseUpdateRequestDTO;
import com.ac.kr.academy.mapper.course.CourseMapper;
import com.ac.kr.academy.mapper.subject.SubjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService{

    private final CourseMapper courseMapper;
    private final SubjectMapper subjectMapper;

    @Override
    @Transactional
    public void addCourse(CourseCreateRequestDTO courseRequestDTO, Long userId) {

        if (courseMapper.existsByTimeAndLocation(courseRequestDTO.getTime(), courseRequestDTO.getLocation())) {
            throw new IllegalArgumentException("해당 시간과 장소에 이미 다른 강의가 존재합니다.");
        }
        Subject newSubject = new Subject();
        newSubject.setName(courseRequestDTO.getSubjectName());
        newSubject.setCredit(courseRequestDTO.getCredit());
        newSubject.setDescription(courseRequestDTO.getDescription());
        newSubject.setDeptId(courseRequestDTO.getDeptId());
        newSubject.setProfessorId(courseRequestDTO.getProfessorId());
        subjectMapper.insert(newSubject);
        courseMapper.insert(courseRequestDTO);
    }

    @Override
    public List<CourseListResponseDTO> findAll(String keyword, String type) {
        if("과목명".equals(type)){
            return courseMapper.findAllBySubjectName(keyword);
        } else if ("학과명".equals(type)){
            return courseMapper.findAllByDeptName(keyword);
        } else {
            return courseMapper.findAll(keyword);
        }
    }

    @Override
    public CourseListResponseDTO findById(Long id) {
        return courseMapper.findById(id);
    }

    @Override
    public void update(CourseUpdateRequestDTO courseUpdateRequestDTO, Long id, Long userId) {
        courseMapper.update(courseUpdateRequestDTO, id);
    }

    @Override
    public void delete(Long id, Long userId) {
        courseMapper.delete(id);
    }
}
