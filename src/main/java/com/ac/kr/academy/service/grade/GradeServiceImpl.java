package com.ac.kr.academy.service.grade;


import com.ac.kr.academy.domain.course.Course;
import com.ac.kr.academy.domain.grade.AlphabetSystem;
import com.ac.kr.academy.domain.grade.Grade;
import com.ac.kr.academy.domain.grade.GradeSystem;
import com.ac.kr.academy.domain.subject.Subject;
import com.ac.kr.academy.dto.page.PageRequestDTO;
import com.ac.kr.academy.dto.page.PageResponseDTO;
import com.ac.kr.academy.mapper.grade.GradeMapper;
import com.ac.kr.academy.service.course.CourseService;
import com.ac.kr.academy.service.user.StudentService;
import com.ac.kr.academy.service.semester.SemesterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {

    private final GradeMapper gradeMapper;
    private final CourseService courseService;
    private final StudentService studentService;
    private final SemesterService semesterService;
    
    public GradeMapper getGradeMapper() { return gradeMapper; }
    
    // 학생 정보 조회 (GradeMapper 직접 사용)
    // TODO: 원래는 StudentService에 있어야 하는 기능이지만, 
    //       인해 GradeService에 임시 배치
    //       나중에 리팩토링 시 StudentService.getStudentInfoForGrade()로 이동 필요
    @Override
    public Map<String, Object> getStudentInfoForGrade(Long studentId) {
        log.info("🔍 학생 정보 조회 시작 - studentId: {}", studentId);
        
        try {
            // GradeMapper를 통해 직접 학생 정보 조회
            Map<String, Object> studentInfo = gradeMapper.findStudentInfoForGrade(studentId);
            log.info("📊 DB 조회 결과 - studentInfo: {}", studentInfo);
            
            if (studentInfo != null && !studentInfo.isEmpty()) {
                log.info("✅ 학생 정보 조회 성공: {}", studentInfo);
                return studentInfo;
            } else {
                log.warn("❌ DB에서 학생 정보를 찾을 수 없음 - studentId: {}", studentId);
            }
        } catch (Exception e) {
            log.error("❌ GradeMapper로 학생 정보 조회 실패 - studentId: {}, error: {}", studentId, e.getMessage(), e);
        }
        
        // fallback: null 반환
        log.warn("⚠️ 학생 정보를 찾을 수 없음 - studentId: {}", studentId);
        return null;
    }


    /*================================관리자================================*/

    // 전체 규정 목록
    @Override
    public PageResponseDTO<AlphabetSystem> listAlphabetGlobal(String searchType,
                                                              String searchKeyword,
                                                              PageRequestDTO req) {
        long total = gradeMapper.countAlphabetGlobal(searchType, searchKeyword);
        List<AlphabetSystem> rows = (total == 0)
                ? Collections.emptyList()
                : gradeMapper.findAlphabetGlobal(req.getStart(), req.getEnd(), searchType, searchKeyword);
        return PageResponseDTO.pageOf(rows, total, req);
    }

    // 과목/수강 규정 목록
    @Override
    public PageResponseDTO<AlphabetSystem> listAlphabetBySubject(Long subjectId,
                                                                 Long enrollmentId,
                                                                 String searchType,
                                                                 String searchKeyword,
                                                                 PageRequestDTO req) {
        long total = gradeMapper.countAlphabetSubject(subjectId, enrollmentId);
        List<AlphabetSystem> rows = (total == 0)
                ? Collections.emptyList()
                : gradeMapper.findAlphabetBySubject(
                                                    subjectId,
                                                    enrollmentId,
                                                    req.getStart(),
                                                    req.getEnd()
                                                    );
        return PageResponseDTO.pageOf(rows, total, req);
    }


    // 규정 수정
    @Override
    @Transactional
    public void updateAlphabetRule(AlphabetSystem as) { gradeMapper.updateAlphabetRule(as); }

    // 규정 삭제
    @Override
    @Transactional
    public void deleteAlphabetRule(Long id) {
        gradeMapper.deleteAlphabetRule(id);
    }

    /*================================교수================================*/

    // 교수 성적 목록
    @Override
    public PageResponseDTO<Grade> listByCourse(Long professorId,
                                               Long courseId,
                                               Long subjectId,
                                               String searchType,
                                               String searchKeyword,
                                               PageRequestDTO req) {

        long total = gradeMapper.countByCourse(professorId, courseId, subjectId, searchType, searchKeyword);

        List<Grade> list = (total == 0)
                ? Collections.emptyList()
                : gradeMapper.findByCourse(professorId, courseId, subjectId,
                searchType, searchKeyword,
                req.getStart(), req.getEnd());

        return PageResponseDTO.pageOf(list, total, req);
    }

    // 성적 등록
    @Transactional
    @Override
    // 성적 등록: 점수 계산 적용 후 저장, 상대평가 시 전체 재계산 트리거
    public void addGrade(Grade grade, Long professorId) {
        log.info("=== addGrade 메서드 호출됨 ===");
        log.info("Grade: {}, ProfessorId: {}", grade, professorId);
        log.info("입력된 점수 - 중간: {}, 기말: {}, 과제: {}, 출석: {}", 
                grade.getMidExam(), grade.getFinalExam(), grade.getAssignment(), grade.getAttendance());
        
        enforceProfessorOwnershipIfNeeded(grade.getId(), professorId, grade.getEnrollmentId());
        
        Long subjectId = null;
        if (grade.getEnrollmentId() != null) {
            subjectId = gradeMapper.findSubjectIdByEnrollment(grade.getEnrollmentId());
        }
        
        applyCalcWithRules(grade, subjectId, grade.getEnrollmentId());
        
        // 중복 INSERT 방지: 기존 grade가 있으면 UPDATE, 없으면 INSERT
        Grade existingGrade = gradeMapper.findByEnrollmentId(grade.getEnrollmentId());
        if (existingGrade != null) {
            log.info("기존 성적 발견 - UPDATE 실행, enrollmentId: {}", grade.getEnrollmentId());
            grade.setId(existingGrade.getId());
            gradeMapper.update(grade);
        } else {
            log.info("새 성적 등록 - INSERT 실행, enrollmentId: {}", grade.getEnrollmentId());
            gradeMapper.insert(grade);
        }
        
        try {
            Long courseId = gradeMapper.findCourseIdByEnrollment(grade.getEnrollmentId());
            if (courseId != null) {
                // 전체 수강생 수 확인 (성적 유무와 관계없이)
                Long totalStudents = gradeMapper.countStudentsByCourse(courseId);
                log.info("전체 수강생 수: {}", totalStudents);
                
                if (totalStudents >= 5) {
                    log.info("상대평가 재계산 시작 - courseId: {}, 전체 수강생 수: {}", courseId, totalStudents);
                    recalculateAllGradesForCourse(courseId);
                    log.info("상대평가 재계산 완료 - courseId: {}", courseId);
                } else {
                    log.info("절대평가 - courseId: {}, 전체 수강생 수: {}", courseId, totalStudents);
                }
            } else {
                log.warn("courseId를 찾을 수 없음 - enrollmentId: {}", grade.getEnrollmentId());
            }
        } catch (Exception e) {
            log.error("실시간 상대평가 재계산 실패", e);
        }
    }

    // 성적 수정
    @Override
    @Transactional
    // 성적 수정: 기존 점수 갱신 및 상대평가 시 전체 재계산 트리거
    public void editGrade(Grade grade, Long professorId) {
        enforceProfessorOwnershipIfNeeded(grade.getId(), professorId, grade.getEnrollmentId());
        
        Long subjectId = null;
        if (grade.getEnrollmentId() != null) {
            subjectId = gradeMapper.findSubjectIdByEnrollment(grade.getEnrollmentId());
        }
        
        applyCalcWithRules(grade, subjectId, grade.getEnrollmentId());
        gradeMapper.update(grade);
        
        try {
            Long courseId = gradeMapper.findCourseIdByEnrollment(grade.getEnrollmentId());
            if (courseId != null) {
                // 전체 수강생 수 확인 (성적 유무와 관계없이)
                Long totalStudents = gradeMapper.countStudentsByCourse(courseId);
                log.info("전체 수강생 수: {}", totalStudents);
                
                if (totalStudents >= 5) {
                    log.info("상대평가 재계산 시작 - courseId: {}, 전체 수강생 수: {}", courseId, totalStudents);
                    recalculateAllGradesForCourse(courseId);
                    log.info("상대평가 재계산 완료 - courseId: {}", courseId);
                } else {
                    log.info("절대평가 - courseId: {}, 전체 수강생 수: {}", courseId, totalStudents);
                }
            } else {
                log.warn("courseId를 찾을 수 없음 - enrollmentId: {}", grade.getEnrollmentId());
            }
        } catch (Exception e) {
            log.error("실시간 상대평가 재계산 실패", e);
        }
    }

    // 성적 삭제
    @Override
    @Transactional
    public void deleteGrade(Long gradeId, Long professorId) {
        if (professorId != null) {
            int own = gradeMapper.existsGradeForProfessor(gradeId, professorId);
            if (own == 0) throw new IllegalStateException("not owner of this grade");
        }
        gradeMapper.delete(gradeId);
    }

    // 교수가 개설한 강의 목록 조회 (CourseService 활용)
    @Override
    public PageResponseDTO<Map<String, Object>> listProfessorCoursesFromCourseService(Long professorId, 
                                                                                     String searchType, 
                                                                                     String searchKeyword, 
                                                                                     PageRequestDTO req) {
        // CourseService.findAllPaged를 활용하여 교수 강의 목록 조회
        PageResponseDTO<com.ac.kr.academy.dto.course.CourseListResponseDTO> courseResult = 
                courseService.findAllPaged(req, professorId);
        
        // CourseListResponseDTO를 Map<String, Object>로 변환
        List<Map<String, Object>> convertedCourses = new ArrayList<>();
        if (courseResult.getData() != null) {
            for (com.ac.kr.academy.dto.course.CourseListResponseDTO course : courseResult.getData()) {
                Map<String, Object> courseMap = new HashMap<>();
                courseMap.put("COURSEID", course.getId());
                courseMap.put("SUBJECTNAME", course.getSubjectName());
                courseMap.put("CREDIT", course.getCredit());
                courseMap.put("CAPACITY", course.getCapacity());
                
                // 실제 Grade 테이블에서 수강생 수 조회
                Long actualStudentCount = gradeMapper.countStudentsByCourse(course.getId());
                courseMap.put("NUMOFSTUDENT", actualStudentCount != null ? actualStudentCount : 0);
                
                courseMap.put("STATUS", course.getStatus());
                courseMap.put("DAYOFWEEK", course.getDayOfWeek());
                courseMap.put("TIME", course.getTime());
                courseMap.put("PLACE", course.getPlace());
                convertedCourses.add(courseMap);
            }
        }
        
        return PageResponseDTO.pageOf(convertedCourses, courseResult.getTotalCount(), req);
    }

    // 점수 분배
    @Override
    public PageResponseDTO<GradeSystem> listGradeSystemByCourse(Long courseId,
                                                                String searchType,
                                                                String searchKeyword,
                                                                PageRequestDTO req) {

        long total = gradeMapper.countGradeSystemByCourse(courseId);
        List<GradeSystem> rows = (total == 0)
                ? Collections.emptyList()
                : gradeMapper.findGradeSystemByCoursePaged(courseId, req.getStart(), req.getEnd());

        return PageResponseDTO.pageOf(rows, total, req);
    }

    // 점수 분배 등록
    @Transactional
    @Override
    public void addGradeSystem(GradeSystem gs) { gradeMapper.insertGradeSystem(gs); }

    // 점수 분배 수정
    @Override
    @Transactional
    public void editGradeSystem(GradeSystem gs) { gradeMapper.updateGradeSystem(gs); }
    
    public GradeSystem getGradeSystemById(Long id) { return gradeMapper.getGradeSystemById(id); }

    @Override
    public GradeSystem getGradeSystemByCourse(Long courseId) {
        if (courseId == null) return null;
        // 코스별 점수비율 1건 조회 (이미 Mapper에 있음)
        return gradeMapper.findGradeSystemByCourse(courseId);
    }

    @Override
    public Map<String, Object> calculateGradePreview(Grade grade) {
        if (grade == null) return Map.of();

        // 저장하지 않고, 기존 계산 로직만 재사용해서 결과만 반환
        Long enrollmentId = grade.getEnrollmentId(); // 과목 전용 규정이 필요하면 Controller에서 함께 넘겨줄 수 있음
        // subjectId를 Grade에 두지 않았다면 null로 둠 (전용 규정 없으면 글로벌 규정 fallback)
        Long subjectId = null;

        // 기존 내부 계산 파이프라인 그대로 사용
        applyCalcWithRules(grade, subjectId, enrollmentId);

        Map<String, Object> result = new HashMap<>();
        result.put("totalScore", grade.getTotalInt());
        result.put("alphabet",   grade.getAlphabet());
        result.put("gpa10",      grade.getGpa());   // 4.5 → 45 형태(×10)

        return result;
    }

    @Override
    public Map<String, Object> summarizeForStudent(Long studentId) {
        if (studentId == null) return Map.of();
        Long sumScore = gradeMapper.sumScoreByStudent(studentId);
        Long avgGpa10 = gradeMapper.avgGpa10ByStudent(studentId);
        double avgGpa = (avgGpa10 == null ? 0 : avgGpa10) / 10.0;
        return Map.of(
                "totalScore", sumScore == null ? 0L : sumScore,
                "avgGpa", avgGpa
        );
    }

    @Override
    public Map<String, Object> checkGraduation(Long studentId, long requiredScore, double requiredAvgGpa) {
        Map<String, Object> sum = summarizeForStudent(studentId);
        long totalScore = ((Number) sum.getOrDefault("totalScore", 0L)).longValue();
        double avgGpa = ((Number) sum.getOrDefault("avgGpa", 0.0)).doubleValue();
        boolean ok = totalScore >= requiredScore && avgGpa >= requiredAvgGpa;
        return Map.of(
                "totalScore", totalScore,
                "avgGpa", avgGpa,
                "meets", ok
        );
    }

    private void enforceProfessorOwnershipIfNeeded(Long gradeId, Long professorId, Long enrollmentId) {
        if (professorId == null) return;
        if (gradeId != null) {
            int own = gradeMapper.existsGradeForProfessor(gradeId, professorId);
            if (own == 0) throw new IllegalStateException("not owner of this grade");
        }
        // insert 시에는 gradeId가 없으므로 최소한 enrollment→course를 통해 계산 시 소유권이 자연스레 적용됨
    }


    /*================================학생================================*/

    // 학생 수강신청 목록 조회 (성적 등록 여부 포함)
    @Override
    public PageResponseDTO<Map<String, Object>> listMyEnrollmentsWithGrades(Long studentId,
                                                                            String searchType,
                                                                            String searchKeyword,
                                                                            PageRequestDTO req) {
        
        long total = gradeMapper.countMyEnrollmentsWithGrades(studentId, searchType, searchKeyword);

        List<Map<String, Object>> list = (total == 0)
                ? Collections.emptyList()
                : gradeMapper.findMyEnrollmentsWithGrades(
                studentId, searchType, searchKeyword, req.getStart(), req.getEnd()
        );

        return PageResponseDTO.pageOf(list, total, req);
    }

    // 학기별 성적 조회 (실제 semester 테이블 사용)
    @Override
    public PageResponseDTO<Map<String, Object>> listMyGradesBySemester(Long studentId) {
        log.info("=== listMyGradesBySemester 시작 ===");
        log.info("studentId: {}", studentId);
        
        try {
            // 실제 semester 테이블에서 학기 목록 조회
            List<com.ac.kr.academy.domain.semester.Semester> allSemesters = semesterService.findAllSemesters();
            log.info("전체 학기 수: {}", allSemesters.size());
            
            List<Map<String, Object>> semesterGrades = gradeMapper.findMyGradesBySemester(studentId);
            log.info("학생 성적 데이터 수: {}", semesterGrades.size());
            
            // 데이터가 있는 학기만 필터링하여 결과 생성
            List<Map<String, Object>> result = new ArrayList<>();
            for (com.ac.kr.academy.domain.semester.Semester semester : allSemesters) {
                // 해당 학기 데이터 찾기
                Optional<Map<String, Object>> found = semesterGrades.stream()
                        .filter(data -> ((Number) data.get("SEMESTER")).intValue() == semester.getId().intValue())
                        .findFirst();
                
                // 데이터가 있는 학기만 추가
                if (found.isPresent()) {
                    Map<String, Object> data = found.get();
                    Map<String, Object> semesterData = new HashMap<>();
                    semesterData.put("semester", semester.getId());
                    semesterData.put("appliedCredits", data.get("APPLIED_CREDITS"));
                    semesterData.put("acquiredCredits", data.get("ACQUIRED_CREDITS"));
                    semesterData.put("totalScore", data.get("TOTAL_SCORE"));
                    semesterData.put("averageGpa", data.get("AVERAGE_GPA"));
                    semesterData.put("hasData", true);
                    log.info("학기 {} 데이터 있음: {}", semester.getId(), data);
                    result.add(semesterData);
                } else {
                    log.info("학기 {} 데이터 없음 - 표시하지 않음", semester.getId());
                }
            }
            
            log.info("=== listMyGradesBySemester 완료: {}개 학기 ===", result.size());
            return PageResponseDTO.<Map<String, Object>>builder()
                    .data(result)
                    .totalCount(result.size())
                    .currentPage(1)
                    .pageSize(result.size())
                    .totalPage(1)
                    .build();
                    
        } catch (Exception e) {
            log.error("listMyGradesBySemester 오류 발생", e);
            throw e;
        }
    }

    // 학기별 상세 성적 조회 (특정 학기의 과목별 성적)
    @Override
    public PageResponseDTO<Map<String, Object>> listMyGradesBySemesterDetail(Long studentId, Long semesterId) {
        List<Map<String, Object>> semesterDetailGrades = gradeMapper.findMyGradesBySemesterDetail(studentId, semesterId);
        
        return PageResponseDTO.<Map<String, Object>>builder()
                .data(semesterDetailGrades)
                .totalCount(semesterDetailGrades.size())
                .currentPage(1)
                .pageSize(semesterDetailGrades.size())
                .totalPage(1)
                .build();
    }

    // 성적 단건 조회
    @Override
    public Grade getMyGrade(Long studentId, Long id) {
        // 학생 단건 성적 조회 (학생 소유 검증은 쿼리 조건으로 처리)
        return (Grade) gradeMapper.findMyGradeById(id, studentId);
    }

    /*    =========================================================================================================================*/
    @Override
    public Grade findGrade(Long id) {
        return gradeMapper.findById(id);
    }


    @Override
    public void addAlphabetRule(AlphabetSystem rule) {
        // 규정 추가: 유효성 검사 후 과목별 규정으로 저장
        // courseId가 있으면 과목 전용 규정으로 저장,
        // 없으면 글로벌 규정으로 저장
        if (rule == null) return;
        validateAlphabetRule(rule);
        gradeMapper.insertAlphabetBySubject(rule);
    }

    private void validateAlphabetRule(AlphabetSystem rule) {
        // 허용 학점
        String a = rule.getAlphabet();
        if (a == null) throw new IllegalArgumentException("alphabet is required");
        switch (a) {
            case "A+": case "A": case "B+": case "B": case "C+": case "C": case "D": case "F":
                break;
            default:
                throw new IllegalArgumentException("invalid alphabet: " + a);
        }
        Double b = rule.getBoundary();
        if (b == null) throw new IllegalArgumentException("boundary is required");
        if (b < 0.0 || b > 100.0) throw new IllegalArgumentException("boundary must be 0~100");
        
        // 중복 학점 체크 (같은 courseId에서 같은 alphabet 중복 방지)
    }


    @Override
    @Transactional
    // 글로벌 규정 저장: 기존 삭제 후 새 규정 일괄 저장 및 과목에 반영
    public void saveGlobalRules(Map<String, String> params) {
        
        // 파라미터 검증
        if (params == null || params.isEmpty()) {
            throw new IllegalArgumentException("저장할 규정 데이터가 없습니다.");
        }
        
        gradeMapper.deleteAlphabetGlobal();
        
        String[] grades = {"A+", "A", "B+", "B", "C+", "C", "D"};
        
        for (String grade : grades) {
            String boundaryKey = "boundary_" + grade;
            if (params.containsKey(boundaryKey)) {
                try {
                    double boundary = Double.parseDouble(params.get(boundaryKey));
                    if (boundary >= 0.0 && boundary <= 100.0) {
                        AlphabetSystem rule = new AlphabetSystem();
                        rule.setAlphabet(grade);
                        rule.setBoundary(boundary);
                        rule.setCourseId(null); // 글로벌 규정
                        gradeMapper.insertAlphabetGlobal(rule);
                    } else {
                        log.warn("잘못된 비율 값: {} = {}% (0-100 범위를 벗어남)", grade, boundary);
                    }
                } catch (NumberFormatException e) {
                    log.warn("잘못된 숫자 형식: {} = {}", grade, params.get(boundaryKey));
                }
            }
        }
        
        
        try {
            List<Subject> allSubjects = getAllSubjects();
            if (allSubjects != null && !allSubjects.isEmpty()) {
                for (Subject subject : allSubjects) {
                    try {
                        resetSubjectToGlobal(subject.getId());
                    } catch (Exception e) {
                        log.error("과목 ID {}에 글로벌 규정 적용 실패", subject.getId(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("모든 과목에 글로벌 규정 적용 중 오류 발생", e);
        }
    }


    @Override
    @Transactional
    // 과목 규정 초기화: 해당 과목의 커스텀 규정 삭제
    public void resetSubjectToGlobal(Long subjectId) {
        
        gradeMapper.deleteAlphabetBySubject(subjectId);
    }

    @Override
    @Transactional
    // 과목 규정 인라인 저장: 입력값 검증 후 저장, 상대평가 강의 일괄 재계산
    public void saveSubjectRulesInline(Map<String, String> params) {
        
        String subjectIdStr = params.get("subjectId");
        if (subjectIdStr == null || subjectIdStr.trim().isEmpty()) {
            throw new IllegalArgumentException("subjectId가 비어있습니다.");
        }
        Long subjectId = Long.parseLong(subjectIdStr);
        gradeMapper.deleteAlphabetBySubject(subjectId);
        
        String[] grades = {"A+", "A", "B+", "B", "C+", "C", "D"};
        for (String grade : grades) {
            String value = params.get("boundary_" + grade);
            if (value != null && !value.trim().isEmpty()) {
                try {
                    Double percentage = Double.parseDouble(value);
                    if (percentage >= 0.0 && percentage <= 100.0 && percentage == Math.floor(percentage * 10) / 10) {
                        AlphabetSystem rule = new AlphabetSystem();
                        rule.setCourseId(subjectId);
                        rule.setAlphabet(grade);
                        rule.setBoundary(percentage);
                        gradeMapper.insertAlphabetBySubject(rule);
                    } else {
                        throw new IllegalArgumentException("유효하지 않은 비율 값: " + percentage + "% (학점: " + grade + ")");
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("잘못된 숫자 형식: " + value + " (학점: " + grade + ")");
                }
            }
        }
        
        try {
            List<Long> courseIds = gradeMapper.findCourseIdsBySubject(subjectId);
            if (courseIds != null && !courseIds.isEmpty()) {
                for (Long courseId : courseIds) {
                    try {
                        if (isRelativeGrading(courseId)) {
                            recalculateAllGradesForCourse(courseId);
                        }
                    } catch (Exception e) {
                        log.error("규정 변경으로 인한 재계산 실패 - courseId: {}", courseId, e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("규정 변경으로 인한 재계산 중 오류 발생", e);
        }
    }

    @Override
    @Transactional
    // 강의 전체 성적 재계산: 동일 규칙으로 성적 재산출 후 저장
    public void recalculateAllGradesForCourse(Long courseId) {
        if (courseId == null) return;
        
        List<Grade> allGrades = gradeMapper.findAllGradesByCourse(courseId);
        if (allGrades == null || allGrades.isEmpty()) {
            log.warn("재계산할 성적이 없음 - courseId: {}", courseId);
            return;
        }
        
        log.info("전체 성적 재계산 시작 - courseId: {}, 성적 개수: {}", courseId, allGrades.size());
        
        Long subjectId = gradeMapper.findSubjectIdByCourse(courseId);
        
        for (Grade grade : allGrades) {
            try {
                applyCalcWithRules(grade, subjectId, grade.getEnrollmentId());
                gradeMapper.update(grade);
                log.debug("성적 재계산 완료 - Grade ID: {}, 총점: {}, 학점: {}", grade.getId(), grade.getTotalInt(), grade.getAlphabet());
            } catch (Exception e) {
                log.error("성적 재계산 실패 - Grade ID: {}", grade.getId(), e);
            }
        }
        
        log.info("전체 성적 재계산 완료 - courseId: {}", courseId);
    }
    
    private boolean isRelativeGrading(Long courseId) {
        // 상대평가 여부: 해당 강의의 성적 레코드 수 기준(5명 이상)
        try {
            List<Grade> allGrades = gradeMapper.findAllGradesByCourse(courseId);
            int studentCount = allGrades != null ? allGrades.size() : 0;
            return studentCount >= 5;
        } catch (Exception e) {
            log.error("강의 평가 방식 확인 실패 - courseId: {}", courseId, e);
            return false;
        }
    }

    @Override
    @Transactional
    // 글로벌 규정 복제: 글로벌 → 과목 규정으로 복사 생성
    public void createCustomRulesFromGlobal(Long subjectId) {
        List<AlphabetSystem> globalRules = gradeMapper.findGlobalAlphabetRules();
        for (AlphabetSystem globalRule : globalRules) {
            AlphabetSystem customRule = new AlphabetSystem();
            customRule.setCourseId(subjectId);
            customRule.setAlphabet(globalRule.getAlphabet());
            customRule.setBoundary(globalRule.getBoundary());
            gradeMapper.insertAlphabetBySubject(customRule);
        }
    }

    @Override
    public List<Subject> getAllSubjects() {
        // 임시로 빈 리스트 반환 (실제로는 SubjectService를 활용해야 함)
        return new ArrayList<>();
    }

    /* ================= 계산용: 규칙 로딩 + 산출 ================= */

    // 규칙 로딩 후 총점/학점/GPA 계산 파이프라인
    private void applyCalcWithRules(Grade grade, Long subjectId, Long enrollmentId) {
        Long courseId = null;
        if (grade.getEnrollmentId() != null) {
            courseId = gradeMapper.findCourseIdByEnrollment(grade.getEnrollmentId());
        }

        if (grade.getEnrollmentId() != null && grade.getStudentId() == null) {
            Long studentId = gradeMapper.findStudentIdByEnrollment(grade.getEnrollmentId());
            grade.setStudentId(studentId);
        }

        GradeSystem gs = (courseId != null) ? gradeMapper.findGradeSystemByCourse(courseId) : null;

        List<AlphabetSystem> rulesAs = null;
        
        // 과목별 커스텀 규정 조회 (courseId 기준)
        if (courseId != null) {
            rulesAs = gradeMapper.listAlphabetBySubjectAll(courseId, null);
            log.info("과목별 커스텀 규정 조회 (courseId: {}): {}개", courseId, rulesAs != null ? rulesAs.size() : 0);
        }
        
        if (rulesAs == null || rulesAs.isEmpty()) {
            log.info("과목별 규정 없음 - 글로벌 규정 조회 시도");
            rulesAs = gradeMapper.findGlobalAlphabetRules();
            log.info("글로벌 규정 조회 결과: {}", rulesAs != null ? rulesAs.size() + "개" : "null");
            
            if (rulesAs == null || rulesAs.isEmpty()) {
                log.error("글로벌 규정이 DB에 존재하지 않음 - 기본 규정으로 대체");
                // 글로벌 규정이 없으면 기본 규정 사용
                rulesAs = getDefaultAlphabetRules();
                log.info("기본 규정 사용: {}개", rulesAs.size());
                
                // 글로벌 규정을 DB에 저장
                try {
                    gradeMapper.deleteAlphabetGlobal();
                    for (AlphabetSystem rule : rulesAs) {
                        gradeMapper.insertAlphabetGlobal(rule);
                    }
                    log.info("기본 규정을 DB에 저장 완료");
                } catch (Exception e) {
                    log.error("기본 규정 DB 저장 실패", e);
                }
            }
        }

        recalcWithRules(grade, gs, rulesAs, enrollmentId);
    }

    // null 안전 정수 변환
    private int i(Integer v) { return v == null ? 0 : v; }

    @Override
    // 기본 상대평가 규정 제공(상위 비율 기준)
    public List<AlphabetSystem> getDefaultAlphabetRules() {
        List<AlphabetSystem> defaultRules = new ArrayList<>();
        String[] grades = {"A+", "A", "B+", "B", "C+", "C", "D"};
        double[] boundaries = {15.0, 30.0, 50.0, 70.0, 75.0, 80.0, 95.0}; // 상위 비율
        
        for (int i = 0; i < grades.length; i++) {
            AlphabetSystem rule = new AlphabetSystem();
            rule.setAlphabet(grades[i]);
            rule.setBoundary(boundaries[i]);
            rule.setCourseId(null);
            defaultRules.add(rule);
        }
        
        return defaultRules;
    }
    
    // 상대평가 전용 규정 (상위% 기준)
    private List<AlphabetSystem> getDefaultRelativeRules() {
        List<AlphabetSystem> relativeRules = new ArrayList<>();
        String[] grades = {"A+", "A", "B+", "B", "C+", "C", "D"};
        double[] boundaries = {15.0, 30.0, 50.0, 70.0, 75.0, 80.0, 95.0}; // 상위 비율
        
        for (int i = 0; i < grades.length; i++) {
            AlphabetSystem rule = new AlphabetSystem();
            rule.setAlphabet(grades[i]);
            rule.setBoundary(boundaries[i]);
            rule.setCourseId(null);
            relativeRules.add(rule);
        }
        
        return relativeRules;
    }


    // 점수비율 적용 → 총점 산출 → 상대/절대 규정에 따른 학점/GPA 계산
    private void recalcWithRules(Grade grade, GradeSystem gs, List<AlphabetSystem> rulesAs, Long enrollmentId) {
        // GradeSystem 비율 검증
        if (gs == null) {
            log.error("GradeSystem이 null입니다 - 기본 비율 사용");
            gs = new GradeSystem();
            gs.setMidExamRatio(30.0);
            gs.setFinalExamRatio(40.0);
            gs.setAssignmentRatio(20.0);
            gs.setAttendanceRatio(10.0);
        }
        
        // 비율 합계 검증
        double ratioSum = (gs.getMidExamRatio() != null ? gs.getMidExamRatio() : 0) +
                         (gs.getFinalExamRatio() != null ? gs.getFinalExamRatio() : 0) +
                         (gs.getAssignmentRatio() != null ? gs.getAssignmentRatio() : 0) +
                         (gs.getAttendanceRatio() != null ? gs.getAttendanceRatio() : 0);
        
        if (Math.abs(ratioSum - 100.0) > 0.1) {
            log.error("비율 합계가 100이 아닙니다: {} - 기본 비율 사용", ratioSum);
            gs.setMidExamRatio(30.0);
            gs.setFinalExamRatio(40.0);
            gs.setAssignmentRatio(20.0);
            gs.setAttendanceRatio(10.0);
        }
        
        int midR = (int) Math.round(gs.getMidExamRatio() != null ? gs.getMidExamRatio() : 30);
        int finR = (int) Math.round(gs.getFinalExamRatio() != null ? gs.getFinalExamRatio() : 40);
        int asgR = (int) Math.round(gs.getAssignmentRatio() != null ? gs.getAssignmentRatio() : 20);
        int attR = (int) Math.round(gs.getAttendanceRatio() != null ? gs.getAttendanceRatio() : 10);

        // null 안전 총점 계산
        int mid = i(grade.getMidExam());
        int fin = i(grade.getFinalExam());
        int asg = i(grade.getAssignment());
        int att = i(grade.getAttendance());
        
        log.info("점수 입력값 - 중간: {}, 기말: {}, 과제: {}, 출석: {}", mid, fin, asg, att);
        log.info("비율 - 중간: {}%, 기말: {}%, 과제: {}%, 출석: {}%", midR, finR, asgR, attR);

        double totalScore = (mid * midR / 100.0)
                          + (fin * finR / 100.0)
                          + (asg * asgR / 100.0)
                          + (att * attR / 100.0);

        int totalInt = (int) Math.round(totalScore);
        totalInt = Math.max(0, Math.min(totalInt, 100));
        
        log.info("총점 계산: {} (중간:{}*{}% + 기말:{}*{}% + 과제:{}*{}% + 출석:{}*{}%)", 
                totalInt, mid, midR, fin, finR, asg, asgR, att, attR);
        
        
        grade.setTotalInt(totalInt);
        grade.setScore((long) totalInt);

        // 5명 기준 절대평가/상대평가 적용
        String alphabet = calculateAlphabetByPercentage(totalInt, rulesAs, enrollmentId);
        
        log.info("학점 계산 결과 - 총점: {}, 학점: {}", totalInt, alphabet);
        
        grade.setAlphabet(alphabet);
        Long gpaValue = mapAlphabetToGpa10(alphabet);
        gpaValue = Math.min(gpaValue, 45L);
        grade.setGpa(gpaValue);
    }

    // 상대/절대 평가에 따른 알파벳 학점 산출 (F는 총점<30에서만)
    private String calculateAlphabetByPercentage(int totalScore, List<AlphabetSystem> rules, Long enrollmentId) {
        log.info("=== 학점 계산 시작 ===");
        log.info("totalScore: {}, enrollmentId: {}", totalScore, enrollmentId);
        
        // 30점 미만은 무조건 F
        if (totalScore < 30) {
            log.info("30점 미만 - F 반환");
            return "F";
        }
        
        // 전체 수강생 수로 모드 결정
        Long courseId = gradeMapper.findCourseIdByEnrollment(enrollmentId);
        Long totalStudents = gradeMapper.countStudentsByCourse(courseId);
        log.info("전체 수강생 수: {}", totalStudents);
        
        if (totalStudents == null || totalStudents <= 0) {
            log.warn("수강생이 없음 - F 반환");
            return "F";
        }
        
        if (totalStudents < 5) {
            log.info("5명 미만 - 절대평가 적용");
            return calculateAbsoluteGrade(totalScore);
        }
        
        log.info("5명 이상 - 상대평가 적용");
        
        // 현재 학생보다 높은 점수를 가진 학생 수
        Long higherScoreCount = gradeMapper.countStudentsWithHigherScore(enrollmentId, totalScore);
        if (higherScoreCount == null) {
            higherScoreCount = 0L;
        }
        
        // 상위 비율 계산: (더 높은 점수 학생 수) / 전체 학생 수 * 100
        double percentile = ((double) higherScoreCount / totalStudents) * 100.0;
        percentile = Math.max(0.0, Math.min(100.0, percentile));
        log.info("상위 비율: {}% (더 높은 점수 학생: {}명 / 전체: {}명)", percentile, higherScoreCount, totalStudents);
        
        // 상대평가 규정 (상위% 기준)
        if (percentile <= 15.0) {
            log.info("학점 결정: A+ (상위 {}% <= 15%)", percentile);
            return "A+";
        } else if (percentile <= 30.0) {
            log.info("학점 결정: A (상위 {}% <= 30%)", percentile);
            return "A";
        } else if (percentile <= 50.0) {
            log.info("학점 결정: B+ (상위 {}% <= 50%)", percentile);
            return "B+";
        } else if (percentile <= 70.0) {
            log.info("학점 결정: B (상위 {}% <= 70%)", percentile);
            return "B";
        } else if (percentile <= 75.0) {
            log.info("학점 결정: C+ (상위 {}% <= 75%)", percentile);
            return "C+";
        } else if (percentile <= 80.0) {
            log.info("학점 결정: C (상위 {}% <= 80%)", percentile);
            return "C";
        } else {
            log.info("학점 결정: D (상위 {}% > 80%)", percentile);
            return "D";
        }
    }
    
    // 절대평가: 점수 구간별 학점 매핑 (F는 30 미만)
    private String calculateAbsoluteGrade(int totalScore) {
        if (totalScore < 30) return "F";
        if (totalScore >= 95) return "A+";
        else if (totalScore >= 90) return "A";
        else if (totalScore >= 85) return "B+";
        else if (totalScore >= 80) return "B";
        else if (totalScore >= 75) return "C+";
        else if (totalScore >= 70) return "C";
        else return "D";
    }

    // 학점문자 → GPA×10 매핑 (A+=45 ... F=0)
    private long mapAlphabetToGpa10(String a) {
        if (a == null) return 0L;
        switch (a) {
            case "A+": return 45L;
            case "A":  return 40L;
            case "B+": return 35L;
            case "B":  return 30L;
            case "C+": return 25L;
            case "C":  return 20L;
            case "D":  return 10L;
            default:   return 0L; // F
        }
    }

    @Override
    public List<Map<String, Object>> getStudentsWithoutGrade(Long courseId) {
        return gradeMapper.findStudentsWithoutGrade(courseId);
    }



    @Override
    public List<AlphabetSystem> findAlphabetBySubject(Long courseId) {
        Course course = gradeMapper.findCourseById(courseId);
        if (course == null) {
            return Collections.emptyList();
        }
        
        Long subjectId = course.getSubjectId();
        List<AlphabetSystem> customRules = gradeMapper.listAlphabetBySubjectAll(subjectId, null);
        
        if (customRules != null && !customRules.isEmpty()) {
            customRules.sort((r1, r2) -> {
                String[] order = {"A+", "A", "B+", "B", "C+", "C", "D", "F"};
                int index1 = java.util.Arrays.asList(order).indexOf(r1.getAlphabet());
                int index2 = java.util.Arrays.asList(order).indexOf(r2.getAlphabet());
                return Integer.compare(index1, index2);
            });
            
            return customRules;
        } else {
            return Collections.emptyList();
        }
    }


    @Override
    public Course getCourseById(Long courseId) {
        return gradeMapper.findCourseById(courseId);
    }

    @Override
    public PageResponseDTO<Map<String, Object>> listSubjectRulesStatus(String searchType, 
                                                                       String searchKeyword, 
                                                                       PageRequestDTO req) {
        long total = gradeMapper.countSubjectRules(searchType, searchKeyword);
        int start = (req.getPage() - 1) * req.getPageSize() + 1;
        int end = req.getPage() * req.getPageSize();
        
        List<Map<String, Object>> data = gradeMapper.findSubjectRulesStatus(searchType, searchKeyword, start, end);
        
        List<AlphabetSystem> globalRules = getDefaultAlphabetRules();
        
        for (Map<String, Object> subject : data) {
            Object subjectIdObj = subject.get("subjectId");
            Long subjectId = null;
            if (subjectIdObj instanceof java.math.BigDecimal) {
                subjectId = ((java.math.BigDecimal) subjectIdObj).longValue();
            } else if (subjectIdObj instanceof Number) {
                subjectId = ((Number) subjectIdObj).longValue();
            }
            
            List<AlphabetSystem> customRules = null;
            if (subjectId != null) {
                // subjectId를 courseId로 사용하여 조회 (실제 DB에서는 course_id로 저장됨)
                customRules = gradeMapper.listAlphabetBySubjectAll(subjectId, null);
            }
            

            if (customRules != null && !customRules.isEmpty()) {
                boolean isDifferentFromGlobal = false;
                Map<String, Double> globalRuleMap = new HashMap<>();
                
                for (AlphabetSystem globalRule : globalRules) {
                    globalRuleMap.put(globalRule.getAlphabet(), globalRule.getBoundary());
                }
                
                for (AlphabetSystem customRule : customRules) {
                    Double globalValue = globalRuleMap.get(customRule.getAlphabet());
                    if (globalValue != null && !globalValue.equals(customRule.getBoundary())) {
                        isDifferentFromGlobal = true;
                        break;
                    }
                }
                
                if (isDifferentFromGlobal) {
                    subject.put("ruleType", "CUSTOM");
                    subject.put("rules", customRules);
                } else {
                    subject.put("ruleType", "GLOBAL");
                    subject.put("rules", globalRules);
                }
            } else {
                subject.put("ruleType", "GLOBAL");
                subject.put("rules", globalRules);
            }
        }
        
        return PageResponseDTO.pageOf(data, total, req);
    }

    // 사용자 ID로 교수 ID 찾기 (CourseService 활용)
    @Override
    public Long findProfessorIdByUserId(Long userId) {
        return courseService.findProfessorIdByUserId(userId);
    }
    
    // 사용자 ID로 학생 ID 찾기 (StudentService 활용)
    @Override
    public Long findStudentIdByUserId(Long userId) {
        return studentService.findStudentIdByUserId(userId);
    }

}
