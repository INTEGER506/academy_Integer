package com.ac.kr.academy.service.course;

import com.ac.kr.academy.dto.course.CourseCreateRequestDTO;
import com.ac.kr.academy.dto.course.CourseDayTimeDTO;
import com.ac.kr.academy.dto.course.CourseListResponseDTO;
import com.ac.kr.academy.dto.course.CourseUpdateRequestDTO;
import com.ac.kr.academy.dto.page.PageRequestDTO;
import com.ac.kr.academy.dto.page.PageResponseDTO;

import java.util.List;

public interface CourseService {

    /** 교수에 의한 강의 개설 */
    void addCourse(CourseCreateRequestDTO courseRequestDTO, Long userId);

    /** 강의 목록 조회 (관리자/교수별 필터링 포함) */
    PageResponseDTO<CourseListResponseDTO> findAllPaged(PageRequestDTO pageRequestDTO, Long filterProfessorId);

    /** 강의 단건 조회 */
    CourseListResponseDTO findById(Long id);

    /** 강의 수정 */
    void update(CourseUpdateRequestDTO courseUpdateRequestDTO, Long id, Long userId);

    /** 강의 삭제 */
    void delete(Long id, Long userId);

    /** 강의 수정 폼용 단건 조회 */
    CourseUpdateRequestDTO findUpdateById(Long id);

    /** 교수의 강의 요일·시간표 조회 */
    List<CourseDayTimeDTO> findCoursesDayTimeByProfessorId(Long professorId);

    /** 관리자에 의한 강의 폐강 처리 */
    void closeCourse(Long courseId, Long userId);

    /** users.id로부터 professor.id 조회 (교수 계정용) */
    Long findProfessorIdByUserId(Long userId);
}
