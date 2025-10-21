package com.ac.kr.academy.service.course;

import com.ac.kr.academy.domain.course.Course;
import com.ac.kr.academy.dto.course.CourseCreateRequestDTO;
import com.ac.kr.academy.dto.course.CourseDayTimeDTO;
import com.ac.kr.academy.dto.course.CourseListResponseDTO;
import com.ac.kr.academy.dto.course.CourseUpdateRequestDTO;
import com.ac.kr.academy.dto.page.PageRequestDTO;
import com.ac.kr.academy.dto.page.PageResponseDTO;
import com.ac.kr.academy.mapper.course.CourseMapper;
import com.ac.kr.academy.mapper.enrollment.EnrollmentMapper;
import com.ac.kr.academy.mapper.semester.SemesterMapper;
import com.ac.kr.academy.mapper.subject.SubjectMapper;
import com.ac.kr.academy.mapper.user.UserMapper;
import com.ac.kr.academy.mapper.user.professor.ProfessorMapper;
import com.ac.kr.academy.service.semester.SemesterService;
import com.ac.kr.academy.service.user.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap; // 순서 유지를 위해 LinkedHashMap 사용
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;
    private final SubjectMapper subjectMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final StudentService studentService;
    private final UserMapper userMapper;
    private final ProfessorMapper professorMapper;
    private final SemesterMapper semesterMapper;
    private final SemesterService semesterService;

    // 강의 시간(시작 시각)을 교시로 매핑하는 상수 맵
    private static final Map<String, String> TIME_TO_CLASS_MAP = Map.ofEntries(
            Map.entry("09:00", "1"),
            Map.entry("10:00", "2"),
            Map.entry("11:00", "3"),
            Map.entry("12:00", "4"),
            Map.entry("13:00", "5"),
            Map.entry("14:00", "6"),
            Map.entry("15:00", "7"),
            Map.entry("16:00", "8"),
            Map.entry("17:00", "9")
    );


    /**
     * 관리자 여부 판단
     */
    private boolean isUserAdmin(Long userId) {
        String userRole = userMapper.findRoleById(userId);
        return "ROLE_ADMIN".equals(userRole);
    }

    /**
     * 교수 계정으로 강의 개설
     */
    @Override
    @Transactional
    public void addCourse(CourseCreateRequestDTO courseRequestDTO, Long userId) {
        // 교수 ID 매핑
        Long professorId = findProfessorIdByUserId(userId);

        // 과목 학점과 강의 횟수(스케줄 수) 검증
        Integer subjectCredit = subjectMapper.findCreditBySubjectId(courseRequestDTO.getSubjectId());
        int scheduleCount = (courseRequestDTO.getScheduleList() != null)
                ? courseRequestDTO.getScheduleList().size()
                : 0;

        if (subjectCredit != null && !subjectCredit.equals(scheduleCount)) {
            throw new IllegalStateException(
                    "해당 과목은 " + subjectCredit + "학점입니다. " +
                            "요일/시간은 " + subjectCredit + "개 입력해야 합니다. (현재 " + scheduleCount + "개)"
            );
        }

        // 여러 요일/시간/강의실을 ,로 합침
        String joinedDays = courseRequestDTO.getScheduleList().stream()
                .map(s -> s.getDayOfWeek())
                .collect(Collectors.joining(","));
        String joinedTimes = courseRequestDTO.getScheduleList().stream()
                .map(s -> s.getTime())
                .collect(Collectors.joining(","));
        String joinedPlaces = courseRequestDTO.getScheduleList().stream()
                .map(s -> s.getPlace())
                .collect(Collectors.joining(","));

        // 중복 체크: 각 스케줄에 대해 개별 확인
        for (CourseCreateRequestDTO.ScheduleDTO schedule : courseRequestDTO.getScheduleList()) {

            // (1) 같은 강의실에 같은 시간대면 전체적으로 금지 (교수 상관없이)
            int roomConflict = courseMapper.existsByDayOfWeekAndPlaceAndTime(
                    schedule.getDayOfWeek(),
                    schedule.getPlace(),
                    schedule.getTime()
            );
            if (roomConflict > 0) {
                throw new IllegalStateException("해당 강의실은 이미 해당 시간에 사용 중입니다. (" +
                        schedule.getDayOfWeek() + " " + schedule.getTime() + ")");
            }

            // (2) 같은 교수가 같은 요일/시간대면 금지 (강의실 상관없이)
            int professorConflict = courseMapper.existsByProfessorDayAndTime(
                    professorId,
                    schedule.getDayOfWeek(),
                    schedule.getTime()
            );
            if (professorConflict > 0) {
                throw new IllegalStateException("교수님의 강의 시간표가 겹칩니다. (" +
                        schedule.getDayOfWeek() + " " + schedule.getTime() + ")");
            }
        }

        // 중복 없으면 등록
        Course course = Course.builder()
                .professorId(professorId)
                .subjectId(courseRequestDTO.getSubjectId())
                .semesterId(courseRequestDTO.getSemesterId())
                .capacity(courseRequestDTO.getCapacity())
                .numOfStudent(0)
                .dayOfWeek(joinedDays)
                .place(joinedPlaces)
                .time(joinedTimes)
                .status("OPEN")
                .build();

        courseMapper.insert(course);
    }


    /** 강의 목록 조회 (페이징 + 교수 필터링 포함) */
    @Override
    public PageResponseDTO<CourseListResponseDTO> findAllPaged(PageRequestDTO requestDTO, Long filterProfessorId) {

        if (filterProfessorId != null) {
            requestDTO.setProfessorId(filterProfessorId);
        }


        long totalCount = courseMapper.countCourses(requestDTO, null);

        List<CourseListResponseDTO> courses = courseMapper.findAllPaged(requestDTO, null);

        List<CourseListResponseDTO> updatedCourses = courses.stream()
                .map(this::convertToSimpleFormat)
                .collect(Collectors.toList());

        return PageResponseDTO.pageOf(updatedCourses, totalCount, requestDTO);
    }

    /** 강의 단건 조회 */
    @Override
    public CourseListResponseDTO findById(Long id) {
        CourseListResponseDTO dto = courseMapper.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));


        return convertToSimpleFormat(dto);
    }

    /** 수정용 강의 조회 */
    @Override
    public CourseUpdateRequestDTO findUpdateById(Long id) {
        Course course = courseMapper.findCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));

        List<String> days = List.of(course.getDayOfWeek().split(","));
        List<String> times = List.of(course.getTime().split(","));
        List<String> places = List.of(course.getPlace().split(","));

        List<CourseUpdateRequestDTO.ScheduleDTO> schedules =
                java.util.stream.IntStream.range(0, days.size())
                        .mapToObj(i -> {
                            CourseUpdateRequestDTO.ScheduleDTO s = new CourseUpdateRequestDTO.ScheduleDTO();
                            s.setDayOfWeek(days.get(i));
                            s.setTime(times.size() > i ? times.get(i) : "");
                            s.setPlace(places.size() > i ? places.get(i) : "");
                            return s;
                        })
                        .collect(Collectors.toList());

        CourseUpdateRequestDTO dto = new CourseUpdateRequestDTO();
        dto.setId(course.getId());
        dto.setCapacity(course.getCapacity());
        dto.setStatus(course.getStatus());
        dto.setScheduleList(schedules);

        return dto;
    }

    /** 강의 수정 */
    @Override
    @Transactional
    public void update(CourseUpdateRequestDTO dto, Long id, Long userId) {
        Course course = courseMapper.findCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));

        if (!course.getProfessorId().equals(findProfessorIdByUserId(userId))) {
            throw new AccessDeniedException("강의를 수정할 권한이 없습니다.");
        }

        // 학점-스케줄 수 검증
        Integer subjectCredit = subjectMapper.findCreditBySubjectId(course.getSubjectId());
        int scheduleCount = dto.getScheduleList() != null ? dto.getScheduleList().size() : 0;

        if (subjectCredit != null && !subjectCredit.equals(scheduleCount)) {
            throw new IllegalStateException(
                    "이 과목은 " + subjectCredit + "학점입니다. " +
                            "요일/시간은 " + subjectCredit + "개 입력해야 합니다. (현재 " + scheduleCount + "개)"
            );
        }

        // 여러 세트를 문자열로 합침
        if (dto.getScheduleList() != null && !dto.getScheduleList().isEmpty()) {
            dto.setDayOfWeek(dto.getScheduleList().stream()
                    .map(s -> s.getDayOfWeek())
                    .collect(Collectors.joining(",")));
            dto.setTime(dto.getScheduleList().stream()
                    .map(s -> s.getTime())
                    .collect(Collectors.joining(",")));
            dto.setPlace(dto.getScheduleList().stream()
                    .map(s -> s.getPlace())
                    .collect(Collectors.joining(",")));
        }

        // 중복체크
        int roomConflict = courseMapper.existsByDayOfWeekAndPlaceAndTimeExcludingId(
                dto.getDayOfWeek(), dto.getPlace(), dto.getTime(), id
        );
        if (roomConflict > 0) {
            throw new IllegalStateException("해당 시간/장소에 이미 다른 강의가 있습니다.");
        }

        int professorConflict = courseMapper.existsByProfessorDayAndTimeExcludingId(
                course.getProfessorId(), dto.getDayOfWeek(), dto.getTime(), id
        );
        if (professorConflict > 0) {
            throw new IllegalStateException("교수님의 해당 시간에 이미 다른 강의가 등록되어 있습니다.");
        }

        courseMapper.update(dto, id);
    }

    /** 강의 삭제 */
    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        Course course = courseMapper.findCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));

        if (!course.getProfessorId().equals(findProfessorIdByUserId(userId))) {
            throw new AccessDeniedException("강의를 삭제할 권한이 없습니다.");
        }

        if (course.getNumOfStudent() > 0) {
            throw new IllegalStateException("수강 신청한 학생이 있어 삭제할 수 없습니다.");
        }

        enrollmentMapper.deleteByCourseId(id);
        courseMapper.delete(id);
    }

    /** 강의 폐강 (관리자 전용) */
    @Override
    @Transactional
    public void closeCourse(Long courseId, Long userId) {
        Course course = courseMapper.findCourseById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));

        // 1️관리자 권한 확인
        if (!isUserAdmin(userId)) {
            throw new AccessDeniedException("폐강 권한이 없습니다. (관리자 전용)");
        }

        // 2️현재 학기 기준 수강신청 기간 중인지 확인
        boolean isEnrollmentPeriod = semesterService.isEnrollmentPeriod();
        if (isEnrollmentPeriod) {
            throw new IllegalStateException("수강신청 기간 중에는 폐강할 수 없습니다.");
        }

        // 3️폐강 로직 실행
        List<Long> studentIds = enrollmentMapper.findStudentIdsByCourseId(courseId);
        Integer credit = subjectMapper.findCreditBySubjectId(course.getSubjectId());

        if (credit == null) {
            throw new IllegalStateException("과목 학점 정보를 찾을 수 없습니다.");
        }

        for (Long studentId : studentIds) {
            studentService.returnCredit(studentId, credit);
        }

        enrollmentMapper.deleteByCourseId(courseId);
        courseMapper.updateStatus(courseId, "CLOSE");
        courseMapper.setNumOfStudentToZero(courseId);
    }


    /** 교수 시간표 조회 */
    @Override
    public List<CourseDayTimeDTO> findCoursesDayTimeByProfessorId(Long professorId) {

        // 현재 학기 ID를 조회
        Long semesterId = semesterService.getCurrentSemester()
                .or(() -> semesterService.getLatestSemester())  // 최신 학기
                .map(s -> s.getId())
                .orElse(null);

        List<CourseDayTimeDTO> rawList = courseMapper.findCoursesDayTimeByProfessorId(professorId, semesterId);

        List<CourseDayTimeDTO> expanded = new ArrayList<>();
        for (CourseDayTimeDTO dto : rawList) {
            String[] days = dto.getDayOfWeek().split(",");
            String[] times = dto.getTime().split(",");
            String[] places = dto.getPlace().split(",");

            for (int i = 0; i < days.length; i++) {
                CourseDayTimeDTO clone = new CourseDayTimeDTO();
                clone.setCourseId(dto.getCourseId());
                clone.setSubjectName(dto.getSubjectName());
                clone.setProfessorName(dto.getProfessorName());
                clone.setDayOfWeek(days[i].trim());

                String currentTime = (times.length > i ? times[i].trim() : "");
                clone.setTime(currentTime);

                clone.setPlace(places.length > i ? places[i].trim() : "");

                if (currentTime.contains("~")) {
                    String[] timeParts = currentTime.split("~");
                    clone.setStartTime(timeParts[0].trim());
                    clone.setEndTime(timeParts[1].trim());
                } else {
                    clone.setStartTime("");
                    clone.setEndTime("");
                }

                expanded.add(clone);
            }
        }
        return expanded;
    }

    /** users.id → professor.id 매핑 */
    @Override
    public Long findProfessorIdByUserId(Long userId) {
        Long professorId = professorMapper.findProfessorIdByUserId(userId);
        if (professorId == null) {
            throw new IllegalStateException("사용자 ID에 해당하는 교수 정보를 찾을 수 없습니다: " + userId);
        }
        return professorId;
    }

    /**
     * 강의 시간 문자열(예: 09:00~09:50)을 교시 번호(예: 1)로 변환합니다.
     * @param time 시간 문자열
     * @return 교시 번호 문자열
     */
    private String timeToClassTime(String time) {
        if (time == null || !time.contains("~")) {
            return "?";
        }
        String startTime = time.split("~")[0].trim();
        return TIME_TO_CLASS_MAP.getOrDefault(startTime, "?");
    }

    /**
     * 강의 시간표를 간결한 형식(예: 월1,3, 화2 / 공학관101호)으로 변환하고 DTO에 설정합니다.
     */
    private CourseListResponseDTO convertToSimpleFormat(CourseListResponseDTO dto) {
        // null 또는 빈 문자열 체크
        if (dto.getDayOfWeek() == null || dto.getDayOfWeek().isEmpty() ||
                dto.getTime() == null || dto.getTime().isEmpty()) {
            dto.setSimpleDayTime("시간 미정");
            dto.setSimplePlace("장소 미정");
            return dto;
        }

        String[] days = dto.getDayOfWeek().split(",");
        String[] times = dto.getTime().split(",");
        String[] places = dto.getPlace().split(",");

        // 요일 + 교시 정보 매핑 (순서 유지를 위해 LinkedHashMap 사용)
        Map<String, List<String>> scheduleMap = new LinkedHashMap<>();
        for (int i = 0; i < days.length; i++) {
            String day = days[i].trim();
            String time = times.length > i ? times[i].trim() : "";

            // '10:00~10:50' -> '2'로 변환
            String classTime = timeToClassTime(time);

            scheduleMap.computeIfAbsent(day, k -> new ArrayList<>()).add(classTime);
        }

        // Map을 '월1,3, 화2' 형식의 문자열로 변환
        String simpleDayTime = scheduleMap.entrySet().stream()
                .map(entry -> entry.getKey() + entry.getValue().stream()
                        .filter(s -> !s.equals("?"))
                        .sorted(Comparator.comparingInt(s -> {
                            try { return Integer.parseInt(s); } catch (NumberFormatException e) { return 99; }
                        }))
                        .collect(Collectors.joining(",")))
                .collect(Collectors.joining(", "));

        // 2. simplePlace 생성: 강의실 정보 중복 제거 ('공학관 101호, 공학관 102호')
        String simplePlace = List.of(places).stream()
                .map(String::trim)
                .distinct() // 강의실 중복 제거
                .collect(Collectors.joining(", "));

        // DTO 필드 설정
        dto.setSimpleDayTime(simpleDayTime); // 강의시간 열에 사용
        dto.setSimplePlace(simplePlace);     // 강의실 열에 사용
        return dto;
    }
}