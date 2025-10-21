package com.ac.kr.academy.service.enrollment;

import com.ac.kr.academy.domain.course.Course;
import com.ac.kr.academy.domain.enrollment.Enrollment;
import com.ac.kr.academy.domain.semester.Semester;
import com.ac.kr.academy.domain.subject.Subject;
import com.ac.kr.academy.dto.course.CourseDayTimeDTO;
import com.ac.kr.academy.dto.enrollment.EnrollmentListResponseDTO;
import com.ac.kr.academy.dto.page.PageRequestDTO;
import com.ac.kr.academy.dto.page.PageResponseDTO;
import com.ac.kr.academy.mapper.course.CourseMapper;
import com.ac.kr.academy.mapper.enrollment.EnrollmentMapper;
import com.ac.kr.academy.mapper.subject.SubjectMapper;
import com.ac.kr.academy.service.semester.SemesterService;
import com.ac.kr.academy.service.user.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentMapper enrollmentMapper;
    private final CourseMapper courseMapper;
    private final SubjectMapper subjectMapper;
    private final StudentService studentService;
    private final SemesterService semesterService;

    // 시간 → 교시 매핑
    private static final Map<String, String> TIME_TO_CLASS_MAP = Map.ofEntries(
            Map.entry("09:00", "1"),
            Map.entry("10:00", "2"),
            Map.entry("11:00", "3"),
            Map.entry("12:00", "4"),
            Map.entry("13:00", "5"),
            Map.entry("14:00", "6"),
            Map.entry("15:00", "7"),
            Map.entry("16:00", "8"),
            Map.entry("17:00", "9"),
            Map.entry("18:00", "10")
    );

    // ✅ 수강 신청 가능 목록 조회 (학생용)
    @Override
    public PageResponseDTO<EnrollmentListResponseDTO> findAvailableCoursesPaged(
            PageRequestDTO pageRequestDTO, Long studentId, Long currentSemesterId) {

        long totalCount = enrollmentMapper.countAvailableCourses(pageRequestDTO, currentSemesterId);
        List<EnrollmentListResponseDTO> list =
                enrollmentMapper.selectAvailableCoursesPaged(pageRequestDTO, studentId, currentSemesterId);

        Semester currentSemester = semesterService.getCurrentSemester().orElse(null);
        boolean isEnrollmentPeriod = currentSemester != null && semesterService.isEnrollmentPeriod(currentSemester);

        for (EnrollmentListResponseDTO dto : list) {
            dto.setCanEnroll(isEnrollmentPeriod);
            setSimpleScheduleFields(dto);
        }

        return PageResponseDTO.pageOf(list, totalCount, pageRequestDTO);
    }

    // 학생 시간표 조회
    @Override
    public List<CourseDayTimeDTO> findStudentTimeTable(Long studentId) {
        // 현재 학기가 없으면 최근 학기 사용
        Long currentSemesterId = semesterService.getCurrentSemester()
                .or(() -> semesterService.getLatestSemester())
                .orElseThrow(() -> new IllegalStateException("조회 가능한 학기가 없습니다."))
                .getId();

        List<CourseDayTimeDTO> rawList = enrollmentMapper.findStudentTimeTable(studentId, currentSemesterId);
        List<CourseDayTimeDTO> expanded = new ArrayList<>();

        for (CourseDayTimeDTO dto : rawList) {
            String[] days = dto.getDayOfWeek().split(",");
            String[] times = dto.getTime().split(",");
            String[] places = dto.getPlace() != null ? dto.getPlace().split(",") : new String[]{""};

            for (int i = 0; i < days.length; i++) {
                CourseDayTimeDTO copy = new CourseDayTimeDTO();
                copy.setCourseId(dto.getCourseId());
                copy.setSubjectName(dto.getSubjectName());
                copy.setProfessorName(dto.getProfessorName());
                copy.setDayOfWeek(days[i].trim());

                String time = times.length > i ? times[i].trim() : "";
                String place = places.length > i ? places[i].trim() : places[0].trim();

                copy.setTime(time);
                copy.setPlace(place);

                expanded.add(copy);
            }
        }

        return expanded;
    }

    // 수강 신청
    @Override
    @Transactional
    public void enroll(Long courseId, Long studentId) {

        Semester currentSemester = semesterService.getCurrentSemester()
                .orElseThrow(() -> new IllegalStateException("현재 수강신청 가능한 학기가 존재하지 않습니다."));

        if (!semesterService.isEnrollmentPeriod(currentSemester)) {
            throw new IllegalStateException(
                    "현재는 수강신청 기간이 아닙니다. (" +
                            currentSemester.getEnrollmentStartDate() + " ~ " +
                            currentSemester.getEnrollmentEndDate() + ")"
            );
        }

        String studentStatus = studentService.findStudentStatus(studentId);

        if (!"ACTIVE".equals(studentStatus)) {
            throw new IllegalStateException("재학 중인 학생만 수강 신청할 수 있습니다. (현재 상태: " + studentStatus + ")");
        }

        // 중복 신청 확인
        Optional<Enrollment> optional = enrollmentMapper.findByCourseIdAndStudentId(courseId, studentId);
        if (optional.isPresent()) {
            throw new IllegalArgumentException("이미 수강 신청한 강의입니다.");
        }

        Course course = courseMapper.findCourseById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));

        int numOfStudent = Optional.ofNullable(course.getNumOfStudent()).orElse(0);
        int capacity = Optional.ofNullable(course.getCapacity()).orElse(0);

        if (numOfStudent >= capacity) {
            throw new IllegalStateException("해당 강의의 정원이 초과되었습니다.");
        }

        Subject subject = subjectMapper.findById(course.getSubjectId());
        if (subject == null) {
            throw new IllegalArgumentException("해당 강좌의 과목을 찾을 수 없습니다. ID: " + course.getSubjectId());
        }

        int currentCredits = Optional.ofNullable(enrollmentMapper.findTotalCreditsByStudentId(studentId)).orElse(0);
        int newCourseCredit = subject.getCredit();

        if (currentCredits + newCourseCredit > 18) {
            throw new IllegalStateException("수강 가능 학점(18학점)을 초과했습니다.");
        }

        // 시간표 중복 확인
        List<CourseDayTimeDTO> enrolledCourses = findStudentTimeTable(studentId);

        String[] newCourseDays = course.getDayOfWeek().split(",");
        String[] newCourseTimes = course.getTime().split(",");

        for (int i = 0; i < newCourseDays.length; i++) {
            String newDay = newCourseDays[i].trim();
            String newTime = newCourseTimes.length > i ? newCourseTimes[i].trim() : "";

            boolean timeConflict = enrolledCourses.stream().anyMatch(existingCourse ->
                    Objects.equals(existingCourse.getDayOfWeek(), newDay) &&
                            Objects.equals(existingCourse.getTime(), newTime)
            );

            if (timeConflict) {
                throw new IllegalStateException("시간표가 겹치는 강의가 있습니다. (요일: " + newDay + ", 시간: " + newTime + ")");
            }
        }

        // 수강 신청 저장
        Enrollment enrollment = new Enrollment();
        enrollment.setCourseId(courseId);
        enrollment.setStudentId(studentId);
        enrollmentMapper.save(enrollment);

        // 인원 증가
        courseMapper.updateNumOfStudent(courseId, 1);
    }

    // ✅ 수강 취소
    @Override
    @Transactional
    public void cancel(Long courseId, Long studentId) {
        Semester currentSemester = semesterService.getCurrentSemester()
                .orElseThrow(() -> new IllegalStateException("현재 수강신청 가능한 학기가 존재하지 않아 수강 취소를 할 수 없습니다."));

        if (!semesterService.isEnrollmentPeriod(currentSemester)) {
            throw new IllegalStateException("수강 신청 기간이 아니므로 취소할 수 없습니다.");
        }

        int deletedRows = enrollmentMapper.deleteByCourseIdAndStudentId(courseId, studentId);
        if (deletedRows == 0) {
            throw new IllegalArgumentException("수강 신청 내역이 존재하지 않습니다.");
        }

        courseMapper.updateNumOfStudent(courseId, -1);

        Course course = courseMapper.findCourseById(courseId)
                .orElseThrow(() -> new IllegalStateException("강의 정보를 찾을 수 없습니다."));

        Subject subject = subjectMapper.findById(course.getSubjectId());
        if (subject == null) {
            throw new IllegalArgumentException("강좌의 과목 정보를 찾을 수 없습니다. ID: " + course.getSubjectId());
        }

        int courseCredit = subject.getCredit();
        studentService.updateCurrentCredits(studentId, -courseCredit);
    }

    // ✅ 기타 조회 메서드
    @Override
    public List<Enrollment> findEnrollmentsByCourseId(Long courseId) {
        return enrollmentMapper.findEnrollmentsByCourseId(courseId);
    }

    @Override
    public List<Enrollment> findAllEnrollments() {
        return enrollmentMapper.findAllEnrollments();
    }

    @Override
    public Optional<Enrollment> findById(Long id) {
        return enrollmentMapper.findById(id);
    }

    @Override
    public int countEnrollmentsByCourseId(Long courseId) {
        return enrollmentMapper.countEnrollmentsByCourseId(courseId);
    }

    @Override
    public int findTotalCreditsByStudentId(Long studentId) {
        return Optional.ofNullable(enrollmentMapper.findTotalCreditsByStudentId(studentId)).orElse(0);
    }

    @Override
    public List<Enrollment> findEnrollmentsByStudentId(Long studentId) {
        return enrollmentMapper.findByStudentId(studentId);
    }

    @Override
    public List<EnrollmentListResponseDTO> findAllMyEnrolledCourses(Long studentId) {
        List<EnrollmentListResponseDTO> list = enrollmentMapper.selectAllMyEnrolledCourses(studentId);

        for (EnrollmentListResponseDTO dto : list) {
            setSimpleScheduleFields(dto);
        }
        return list;
    }

    // ✅ 시간 문자열을 교시 번호로 변환
    private String timeToClassTime(String time) {
        if (time == null || !time.contains("~")) return "?";
        String startTime = time.split("~")[0].trim();
        return TIME_TO_CLASS_MAP.getOrDefault(startTime, "?");
    }

    // ✅ 요약 필드 설정
    private EnrollmentListResponseDTO setSimpleScheduleFields(EnrollmentListResponseDTO dto) {
        if (dto.getDayOfWeek() == null || dto.getDayOfWeek().isEmpty()
                || dto.getTime() == null || dto.getTime().isEmpty()
                || dto.getPlace() == null || dto.getPlace().isEmpty()) {
            dto.setSimpleDayTime("시간 미정");
            dto.setSimplePlace("장소 미정");
            return dto;
        }

        String[] days = dto.getDayOfWeek().split(",");
        String[] times = dto.getTime().split(",");
        String[] places = dto.getPlace().split(",");

        Map<String, List<String>> scheduleMap = new LinkedHashMap<>();
        for (int i = 0; i < days.length; i++) {
            String day = days[i].trim();
            String time = times.length > i ? times[i].trim() : "";
            String classTime = timeToClassTime(time);
            scheduleMap.computeIfAbsent(day, k -> new ArrayList<>()).add(classTime);
        }

        String simpleDayTime = scheduleMap.entrySet().stream()
                .map(entry -> entry.getKey() + entry.getValue().stream()
                        .filter(s -> !s.equals("?"))
                        .sorted(Comparator.comparingInt(Integer::parseInt))
                        .collect(Collectors.joining(",")))
                .collect(Collectors.joining(", "));

        String simplePlace = Arrays.stream(places)
                .map(String::trim)
                .distinct()
                .collect(Collectors.joining(", "));

        dto.setSimpleDayTime(simpleDayTime);
        dto.setSimplePlace(simplePlace);
        return dto;
    }
}
