package com.ac.kr.academy.service.course;

import com.ac.kr.academy.domain.course.Course;
import com.ac.kr.academy.domain.subject.Subject;
import com.ac.kr.academy.dto.course.CourseCreateRequestDTO;
import com.ac.kr.academy.dto.course.CourseListResponseDTO;
import com.ac.kr.academy.dto.course.CourseUpdateRequestDTO;
import com.ac.kr.academy.mapper.course.CourseMapper;
import com.ac.kr.academy.mapper.enrollment.EnrollmentMapper;
import com.ac.kr.academy.mapper.subject.SubjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService{

    private final CourseMapper courseMapper;
    private final SubjectMapper subjectMapper;
    private final EnrollmentMapper enrollmentMapper;

    @Override
    @Transactional
    public void addCourse(CourseCreateRequestDTO courseRequestDTO, Long userId) {

        if (courseMapper.existsByDayOfWeekAndPlaceAndTime(
                courseRequestDTO.getDayOfWeek(),
                courseRequestDTO.getPlace(),
                courseRequestDTO.getTime())) {
            throw new IllegalArgumentException("해당 시간과 장소에 이미 다른 강의가 존재합니다.");
        }
        Course course = new Course();
        course.setProfessorId(courseRequestDTO.getProfessorId());
        course.setSubjectId(courseRequestDTO.getSubjectId());
        course.setSemesterId(courseRequestDTO.getSemesterId());
        course.setCapacity(courseRequestDTO.getCapacity());
        course.setNumOfStudent(0); // 강의 개설 시 수강 인원은 0명
        course.setDayOfWeek(courseRequestDTO.getDayOfWeek());
        course.setPlace(courseRequestDTO.getPlace());
        course.setStatus("개설"); // 강의 상태는 기본적으로 "개설"
        course.setTime(courseRequestDTO.getTime());
        courseMapper.insert(course);
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
        Course course = courseMapper.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));
        return null;
    }

    @Override
    public void update(CourseUpdateRequestDTO courseUpdateRequestDTO, Long id, Long userId) {

        Course course = new Course();
        course.setId(id);
        course.setCapacity(courseUpdateRequestDTO.getCapacity());
        course.setDayOfWeek(courseUpdateRequestDTO.getDayOfWeek());
        course.setPlace(courseUpdateRequestDTO.getPlace());
        course.setTime(courseUpdateRequestDTO.getTime());
        courseMapper.update(course);
    }

    @Override
    public void delete(Long id, Long userId) {
        enrollmentMapper.deleteByCourseId(id);
        courseMapper.delete(id);
    }

    @Override
    public void closeCourse(Long courseId, Long userId) {

        Course course = courseMapper.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));
//        if (!userService.isAdmin(userId)){
//            throw new AccessDeniedException("폐강 권한이 없습니다.");
//        }
        enrollmentMapper.deleteByCourseId(courseId);

        courseMapper.updateStatus(courseId, "폐강");
    }
}
