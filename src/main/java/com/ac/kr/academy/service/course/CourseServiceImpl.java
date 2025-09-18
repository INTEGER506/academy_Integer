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

        // 모든 제약 조건을 제거하고 강의 개설만 가능하게 수정

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

    // 기존 강의 정보를 DTO로 변환하여 반환
    @Override
    public CourseUpdateRequestDTO findUpdateById(Long id) {
        Course course = courseMapper.findCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));
        return CourseUpdateRequestDTO.builder()
                .id(course.getId())
                .capacity(course.getCapacity())
                .place(course.getPlace())
                .dayOfWeek(course.getDayOfWeek())
                .status(course.getStatus())
                .time(course.getTime())
                .build();
    }

    @Override
    @Transactional
    public void update(CourseUpdateRequestDTO courseUpdateRequestDTO, Long id, Long userId) {
        Course course = courseMapper.findCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));

        // 강의 업데이트 권한 확인
//         if (!course.getProfessorId().equals(userId)) {
//             throw new AccessDeniedException("강의를 수정할 권한이 없습니다.");
//         }
//
//        int studentCount = enrollmentMapper.countEnrollmentsByCourseId(id);
//        if (courseUpdateRequestDTO.getCapacity() != null && courseUpdateRequestDTO.getCapacity() < studentCount) {
//            throw new IllegalArgumentException("변경하려는 정원이 현재 수강 인원보다 적습니다.");
//        }

        courseMapper.update(courseUpdateRequestDTO, id);
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        Course course = courseMapper.findCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));

        // 강의 삭제 권한 확인
        // if (!course.getProfessorId().equals(userId)) {
        //     throw new AccessDeniedException("강의를 삭제할 권한이 없습니다.");
        // }

//        int studentCount = enrollmentMapper.countEnrollmentsByCourseId(id);
//
//        if (studentCount > 0) {
//            throw new IllegalArgumentException("수강 신청한 학생이 있어 강의를 삭제할 수 없습니다. 폐강 기능을 이용해주세요.");
//        }

        enrollmentMapper.deleteByCourseId(id);
        courseMapper.delete(id);
    }

    @Override
    @Transactional
    public void closeCourse(Long courseId, Long userId) {
        Course course = courseMapper.findCourseById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));

        // 폐강 권한 확인 (강의를 개설한 교수만 폐강 가능)
        // if (!course.getProfessorId().equals(userId)) {
        //     throw new AccessDeniedException("폐강 권한이 없습니다.");
        // }

        enrollmentMapper.deleteByCourseId(courseId);
        courseMapper.updateStatus(courseId, "폐강");
        courseMapper.updateNumOfStudent(courseId, 0); // 폐강 시 수강생 수 0으로 초기화
    }
}
