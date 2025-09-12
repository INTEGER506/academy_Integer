package com.ac.kr.academy.service.course;

import com.ac.kr.academy.domain.course.Course;
import com.ac.kr.academy.dto.course.CourseCreateRequestDTO;
import com.ac.kr.academy.dto.course.CourseListResponseDTO;
import com.ac.kr.academy.dto.course.CourseUpdateRequestDTO;
import com.ac.kr.academy.mapper.course.CourseMapper;
import com.ac.kr.academy.mapper.enrollment.EnrollmentMapper;
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
    private final EnrollmentMapper enrollmentMapper;

    @Override
    @Transactional
    public void addCourse(CourseCreateRequestDTO courseRequestDTO, Long userId) {

//        int count = courseMapper.existsByDayOfWeekAndPlaceAndTime(
//                courseRequestDTO.getDayOfWeek(),
//                courseRequestDTO.getPlace(),
//                courseRequestDTO.getTime());
//
//        if (count > 0) {
//            throw new IllegalArgumentException("해당 시간과 장소에 이미 다른 강의가 존재합니다.");
//        }

        // DTO를 Course 엔티티로 변환
        Course course = Course.builder()
                .professorId(courseRequestDTO.getProfessorId())
                .subjectId(courseRequestDTO.getSubjectId())
                .semesterId(courseRequestDTO.getSemesterId())
                .capacity(courseRequestDTO.getCapacity())
                .numOfStudent(0)
                .dayOfWeek(courseRequestDTO.getDayOfWeek())
                .place(courseRequestDTO.getPlace())
                .status("개설")
                .time(courseRequestDTO.getTime())
                .build();

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
        return courseMapper.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));
    }

    @Override
    public void update(CourseUpdateRequestDTO courseUpdateRequestDTO, Long id, Long userId) {
        courseMapper.update(courseUpdateRequestDTO, id);
    }

    @Override
    public void delete(Long id, Long userId) {
        enrollmentMapper.deleteByCourseId(id);
        courseMapper.delete(id);
    }

    @Override
    public void closeCourse(Long courseId, Long userId) {

        Course course = courseMapper.findCourseById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));
//        if (!userService.isAdmin(userId)){
//            throw new AccessDeniedException("폐강 권한이 없습니다.");
//        }
        enrollmentMapper.deleteByCourseId(courseId);

        courseMapper.updateStatus(courseId, "폐강");
    }
}
