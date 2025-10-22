package com.ac.kr.academy.mapper.course;

import com.ac.kr.academy.domain.course.Course;
import com.ac.kr.academy.dto.course.CourseDayTimeDTO;
import com.ac.kr.academy.dto.course.CourseListResponseDTO;
import com.ac.kr.academy.dto.course.CourseUpdateRequestDTO;
import com.ac.kr.academy.dto.page.PageRequestDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CourseMapper {

    /** 강의 개설 */
    void insert(Course course);

    /** 페이징된 강의 목록 조회 (검색 조건 포함) */
    List<CourseListResponseDTO> findAllPaged(
            @Param("requestDTO") PageRequestDTO requestDTO,
            @Param("studentId") Long studentId);

    /** 전체 강의 개수 (검색 조건 포함) */
    long countCourses(
            @Param("requestDTO") PageRequestDTO requestDTO,
            @Param("studentId") Long studentId);

    /** 단일 강의 조회 */
    Optional<CourseListResponseDTO> findById(@Param("id") Long id);

    /** 강의 수정 */
    void update(@Param("dto") CourseUpdateRequestDTO courseUpdateRequestDTO,
                @Param("id") Long id);

    /** 강의 삭제 */
    void delete(@Param("id") Long id);

    /** 요일/강의실/시간 중복 여부 확인 */
    int existsByDayOfWeekAndPlaceAndTime(@Param("dayOfWeek") String dayOfWeek,
                                         @Param("place") String place,
                                         @Param("time") String time);

    /** 수정 시 자기 자신 제외하고 중복 체크 */
    int existsByDayOfWeekAndPlaceAndTimeExcludingId(@Param("dayOfWeek") String dayOfWeek,
                                                    @Param("place") String place,
                                                    @Param("time") String time,
                                                    @Param("id") Long courseId);

    /** 강의 학점 조회 */
    Integer findCreditByCourseId(@Param("courseId") Long courseId);

    /** 수강 인원 변경 (+1 / -1) */
    void updateNumOfStudent(@Param("courseId") Long courseId,
                            @Param("change") int change);

    /** 강의 상태 변경 (예: OPEN → CLOSE) */
    void updateStatus(@Param("courseId") Long courseId,
                      @Param("status") String status);

    /** 강의 엔티티 조회 */
    Optional<Course> findCourseById(@Param("id") Long id);

    /** 교수의 강의 목록 조회 */
    List<CourseListResponseDTO> findCoursesWithSubjectByProfessor(@Param("professorId") Long professorId);

    /** 교수의 시간표 조회 */
    List<CourseDayTimeDTO> findCoursesDayTimeByProfessorId(@Param("professorId") Long professorId,
                                                           @Param("semesterId") Long currentSemesterId);

    /** 교수가 해당 요일/시간에 이미 강의 중인지 확인 */
    int existsByProfessorDayAndTime(@Param("professorId") Long professorId,
                                    @Param("dayOfWeek") String dayOfWeek,
                                    @Param("time") String time);

    /** 강의 수정 시, 자신 제외 중복 체크 */
    int existsByProfessorDayAndTimeExcludingId(@Param("professorId") Long professorId,
                                               @Param("dayOfWeek") String dayOfWeek,
                                               @Param("time") String time,
                                               @Param("id") Long courseId);

    /** 강의 폐강 */
    void closeCourse(@Param("id") Long id);

    /** 수강 인원 초기화 (폐강 시) */
    void setNumOfStudentToZero(@Param("courseId") Long courseId);
}
