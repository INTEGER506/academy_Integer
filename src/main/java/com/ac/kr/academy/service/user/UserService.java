package com.ac.kr.academy.service.user;

import com.ac.kr.academy.domain.dept.Dept;
import com.ac.kr.academy.domain.user.*;
import com.ac.kr.academy.dto.user.MyPageInfoDTO;
import com.ac.kr.academy.mapper.admin.AdminMapper;
import com.ac.kr.academy.mapper.dept.DeptMapper;
import com.ac.kr.academy.mapper.user.UserMapper;
import com.ac.kr.academy.mapper.user.advisor.AdvisorMapper;
import com.ac.kr.academy.mapper.user.professor.ProfessorMapper;
import com.ac.kr.academy.mapper.user.staff.StaffMapper;
import com.ac.kr.academy.mapper.user.student.StudentMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final StudentMapper studentMapper;
    private final ProfessorMapper professorMapper;
    private final StaffMapper staffMapper;
    private final AdvisorMapper advisorMapper;
    private final AdminMapper adminMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final DeptMapper deptMapper;

    private final ObjectMapper objectMapper;

    //로그인 ID 자동생성 - 관리자 / 데이터 준비
    @Transactional
    public Map<String, String> createUser(String role, Long deptId, String email, String name){
        //역할에 따라 순번 키와 최종 사용자 ID(username)을 생성 - AdminMapper
        String sequenceKey;
        String username;

        if ("ROLE_STUDENT".equals(role)) {
            String formatedDeptId = String.format("%02d", deptId);
            sequenceKey = Year.now().toString() + formatedDeptId;
            adminMapper.incrementSequence(sequenceKey);
            int sequence = adminMapper.findSequenceNum(sequenceKey);
            username = sequenceKey + String.format("%03d", sequence);
        } else {
            //ROLE 에 따라 명확한 단축어 sequenceKey 지정
            switch (role) {
                case "ROLE_PROFESSOR":
                case "ROLE_ADVISOR":
                    sequenceKey = "PROF";
                    break;
                case "ROLE_STAFF":
                    sequenceKey = "STF";
                    break;
                default:
                    sequenceKey = role.replace("ROLE_", "");    //예외 처리 (또는 오류발생 시)
            }

            adminMapper.incrementSequence(sequenceKey);
            int sequence = adminMapper.findSequenceNum(sequenceKey);
            username = sequenceKey + String.format("%04d", sequence);
        }

        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setRole(role);
        user.setUsername(username);

        //임시 비밀번호 생성 및 암호화
        String tempPassword = UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setPasswordTemp(true);

        Object roleEntity = null;
        if("ROLE_STUDENT".equals(role)){
            Student student = new Student();
            student.setStudentNum(username);
            student.setDeptId(deptId);
            roleEntity = student;
        } else if ("ROLE_PROFESSOR".equals(role)) {
            Professor professor = new Professor();
            professor.setProfessorNum(username);
            professor.setDeptId(deptId);
            roleEntity = professor;
        } else if ("ROLE_STAFF".equals(role)) {
            Staff staff = new Staff();
            staff.setStaffNum(username);
            roleEntity = staff;
        }
        saveUser(user, roleEntity);

        //로그인 ID와 임시 비밀번호 반환 -> Map 사용
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("username", username);
        userInfo.put("password", tempPassword);

        return userInfo;
    }

    //사용자 등록 / DB 저장
    @Transactional
    public void saveUser(User user, Object roleEntity){
        userMapper.insertUser(user);

        //역할에 따라 상세정보 각 테이블 등록
        if("ROLE_STUDENT".equals(user.getRole())){
            Student student = (Student) roleEntity;
            student.setUserId(user.getId());
            student.setStudentNum(user.getUsername());
            studentMapper.insertStudent(student);
        } else if ("ROLE_PROFESSOR".equals(user.getRole()) || "ROLE_ADVISOR".equals(user.getRole())) {
            Professor professor = (Professor) roleEntity;
            professor.setUserId(user.getId());
            professor.setProfessorNum(user.getUsername());
            professorMapper.insertProfessor(professor);
        } else if ("ROLE_STAFF".equals(user.getRole())) {
            Staff staff = (Staff) roleEntity;
            staff.setUserId(user.getId());
            staff.setStaffNum(user.getUsername());
            staffMapper.insertStaff(staff);
        }
    }

    //지도교수 등록(지정)
//    @Transactional
    public void setAdvisor(Long professorId, Long studentId){
        advisorMapper.insertAdvisor(professorId, studentId);
    }

    //특정 교수를 특정 학과의 모든 학생의 지도교수로 일괄 지정 - 관리자
    @Transactional
    public String assignAdvisorByDeptId(Long professorUserId, Long deptId){
        Long professorId = professorMapper.findProfessorIdByUserId(professorUserId);
        User professorUser = userMapper.findById(professorUserId);
        String professorName = professorUser != null ? professorUser.getName() : "알 수 없는 교수";

        String deptName = deptMapper.findDeptNameById(deptId);

        if(professorId == null || professorUser == null){
            throw new IllegalArgumentException("해당 userId의 교수 정보를 찾을 수 없습니다.");
        }
        if(deptName == null){
            throw new IllegalArgumentException("학과 정보를 찾을 수 없습니다.");
        }

        //해당 학과 모든 학생의 id 조회
        List<Long> studentIds = studentMapper.findAllStudentIdByDeptId(deptId);

        if(studentIds.isEmpty()){
            return String.format("%s 교수님께 지정할 %s 학과 소속 학생이 없습니다.", professorName, deptName);
        }

        //지도교수 관계 설정 (advisor 테이블에 삽입)
        int assignedCount = 0;
        for(Long studentId : studentIds){
            try{
                setAdvisor(professorId, studentId);
                assignedCount++;
            }catch (Exception e){
                //이미 지도교수로 지정된 경우는 건너뛰고 로그만 기록
                log.error("학생 ID " + studentId + "지도교수 지정 실패: " +e.getMessage());
            }
        }

        //권한 변경 - userMapper에 있는 updateUser 활용
        String roleStatus;
        if(assignedCount > 0){
            //ID와 Role만 설정
            User userToUpdate = new User();
            userToUpdate.setId(professorUserId);
            userToUpdate.setRole("ROLE_ADVISOR");

            userMapper.updateUser(userToUpdate);
            roleStatus = "ROLE_ADVISOR로 변경되었습니다.";
        } else {
            roleStatus = "ROLE_PROFESSOR로 유지됩니다. (지정된 학생 없음)";
        }

        return String.format("%s 교수님을 %s 소속 총 %d 명의 학생 지도교수로 지정했고, 권한은 %s",
                professorName, deptName, assignedCount, roleStatus);
    }

    //ROLE_ADVISOR, ROLE_PROFESSOR 목록 조회 - advisor-role.jsp 에서 사용
    public List<User> findAllProfessorsAndAdvisors(){
        List<User> professors = userMapper.findAllUsersByRole("ROLE_PROFESSOR");
        List<User> advisors = userMapper.findAllUsersByRole("ROLE_ADVISOR");

        //두 리스트 합치기 - 중복방지 없이 단순히 합치기
        List<User> allProfessors = new ArrayList<>(professors);
        allProfessors.addAll(advisors);

        return allProfessors;
    }

    //모든 학과 목록 조회 - advisor-role.jsp 에서 사용
    public List<Dept> findAllDepts(){
        return deptMapper.findAll();
    }

    //지도교수 권한 회수 (지도교수 -> 교수)
    @Transactional
    public String revertAdvisorRole(Long professorUserId){
        Long professorId = professorMapper.findProfessorIdByUserId(professorUserId);
        User professorUser = userMapper.findById(professorUserId);
        String professorName = professorUser != null ? professorUser.getName() : "알 수 없는 교수";

        if(professorId == null || professorUser == null){
            throw new IllegalArgumentException("해당 사용자 ID에 해당하는 교수 정보를 찾을 수 없습니다.");
        }

        //advisor 테이블에서 삭제 (삭제된 행의 수 deletedCount에 저장)
        int deletedCount = advisorMapper.deleteAdvisorByProfessorId(professorId);

        //users 테이블에서 권한 ROLE_PROFESSOR로 변경
        User userToUpdate = new User();
        userToUpdate.setId(professorUserId);
        userToUpdate.setRole("ROLE_PROFESSOR");

        userMapper.updateUser(userToUpdate);

        //결과 메세지 반환
        return String.format(
                "%s 교수님(ID: %d)의 지도 교수 권한이 해제되었으며, 총 %d명의 학생과의 관계가 삭제 되었습니다. 권한 또한 ROLE_PROFESSOR로 변경되었습니다.",
                professorName, professorUserId, deletedCount);
    }

    //사용자 수정
    @Transactional
    public void updateUser(User user, Object roleEntity){
        userMapper.updateUser(user);

        //roleEntity가 null이 아닐때만 업데이트 시도 - NullPointerException 방지 위해 추가
        if(roleEntity != null) {
            //object 타입인 roleEntity를 ObjectMapper를 이용해 형변환
            if (roleEntity instanceof LinkedHashMap && !((LinkedHashMap) roleEntity).isEmpty()) {
                if ("ROLE_STUDENT".equals(user.getRole())) {
                    Student student = objectMapper.convertValue(roleEntity, Student.class);
                    if (student.getDeptId() != null || student.getEndedAt() != null || student.getStatus() != null) {
                        studentMapper.updateStudent(student);
                    }
                } else if ("ROLE_PROFESSOR".equals(user.getRole()) || "ROLE_ADVISOR".equals(user.getRole())) {
                    Professor professor = objectMapper.convertValue(roleEntity, Professor.class);
                    if (professor.getDeptId() != null || professor.getEndedAt() != null) {
                        professorMapper.updateProfessor(professor);
                    }
                } else if ("ROLE_STAFF".equals(user.getRole())) {
                    Staff staff = objectMapper.convertValue(roleEntity, Staff.class);
                    if (staff.getEndedAt() != null) {
                        staffMapper.updateStaff(staff);
                    }
                }
            } else {    //object 타입일때
                if ("ROLE_STUDENT".equals(user.getRole())) {
                    Student student = (Student) roleEntity;
                    if (student.getDeptId() != null || student.getEndedAt() != null || student.getStatus() != null) {
                        studentMapper.updateStudent(student);
                    }
                } else if ("ROLE_PROFESSOR".equals(user.getRole()) || "ROLE_ADVISOR".equals(user.getRole())) {
                    Professor professor = (Professor) roleEntity;
                    if (professor.getDeptId() != null || professor.getEndedAt() != null) {
                        professorMapper.updateProfessor(professor);
                    }
                } else if ("ROLE_STAFF".equals(user.getRole())) {
                    Staff staff = (Staff) roleEntity;
                    if (staff.getEndedAt() != null) {
                        staffMapper.updateStaff(staff);
                    }
                }
            }
        }
    }

    //마이페이지 정보 조회 / MyPageInfoDTO 반환
    @Transactional(readOnly = true)
    public MyPageInfoDTO findMyPageInfo(Long userId, String role){
        //역할에 따른 상세정보 조회
        MyPageInfoDTO dto = null;

        if("ROLE_STUDENT".equals(role)){
            dto = studentMapper.findMyPageInfoByUserId(userId);
        } else if("ROLE_PROFESSOR".equals(role) || "ROLE_ADVISOR".equals(role)){
            dto = professorMapper.findMyPageInfoByUserId(userId);
        } else if ("ROLE_STAFF".equals(role) || "ROLE_ADMIN".equals(role)) {
            dto = staffMapper.findMyPageInfoByUserId(userId);
        }

        //Mapper 에서 결과가 없거나, MyPageInfoDTO에 User 기본 정보가 부족할 경우 UserMapper 에서 보충 - admin 방어 코드
        if(dto == null || dto.getName() == null){
            User user = userMapper.findById(userId);
            if(user != null){
                if(dto == null) dto = new MyPageInfoDTO();
                dto.setUserId(user.getId());
                dto.setUsername(user.getUsername());
                dto.setName(user.getName());
                dto.setRole(user.getRole());
                dto.setEmail(user.getEmail());
                dto.setPhone(user.getPhone());
            }
        }
        return dto;
    }

    //마이페이지 전용 - 사용자 수정 (이메일, 전화번호)
    @Transactional
    public void updateUserBasicInfo(MyPageInfoDTO myPageInfoDTO) {
        User user = userMapper.findById(myPageInfoDTO.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        user.setEmail(myPageInfoDTO.getEmail());
        user.setPhone(myPageInfoDTO.getPhone());

        userMapper.updateUser(user);
    }


    //사용자 삭제 - 관리자
    @Transactional
    public void deleteUser(Long userId){
        //user의 role 정보 가져오기
        User user = userMapper.findById(userId);

        if(user==null){
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        if("ROLE_STUDENT".equals(user.getRole())){
            studentMapper.deleteStudent(userId);
        } else if ("ROLE_PROFESSOR".equals(user.getRole()) || "ROLE_ADVISOR".equals(user.getRole())) {
            professorMapper.deleteProfessor(userId);
        } else if ("ROLE_STAFF".equals(user.getRole())) {
            staffMapper.deleteStaff(userId);
        }
        userMapper.deleteUser(userId);
    }

    //사용자 전체 조회
    public List<User> getAllUsers(){
        return userMapper.findAllUsers();
    }

    //권한별 사용자 조회
    public List<User> getUsersByRole(String role){
        return userMapper.findAllUsersByRole(role);
    }

    public User findByUsername(String username){
        return userMapper.findByUsername(username);
    }

    public User findById(Long id){
        return userMapper.findById(id);
    }

    //비밀번호 변경 - 사용자
    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword){
        User user = userMapper.findByUsernameForLogin(username);
        if(user == null){
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        if(!passwordEncoder.matches(currentPassword, user.getPassword())){
            throw new IllegalArgumentException("입력하신 비밀번호가 일치하지 않습니다.");
        }

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        userMapper.updateUserPassword(username, encodedNewPassword, false);
    }

    //비밀번호 초기화 - 관리자
    @Transactional
    public String resetPassword(String username){
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        String tempPassword = UUID.randomUUID().toString().substring(0, 8);
        userMapper.updateUserPassword(username, passwordEncoder.encode(tempPassword), true);
        return tempPassword;
    }

    //비밀번호 찾기
    @Transactional
    public boolean resetPasswordByUserInfo(String username, String name, String email, String newPassword){
        //로그인 ID, 이름, 이메일로 사용자 존재 및 일치 여부 검증
        User user = userMapper.findUserForPasswordRecovery(username, name, email);

        if(user == null){
            //하나라도 불일치 시 실패
            return false;
        }

        //새 비밀번호 해시(암호화)
        String encodedNewPassword = passwordEncoder.encode(newPassword);

        //비밀번호 업데이트 (updateUserPassword 메서드 활용)
        userMapper.updateUserPassword(username, encodedNewPassword, false);

        return true;
    }

    //학생 상태값 변경 (재학중, 휴학중, 졸업)
    @Transactional
    public void updateStudentStatus(Long userId, String status){
        studentMapper.updateStatusByUserId(userId, status);
    }

    //지도교수 권한 확인 메서드 - 특정 교수가 지도교수 권한을 가지고 있는지 확인
//    public boolean isAdvisor(Long professorId){
//        //해당 교수가 지도하는 학생 목록
//        List<Advisor> adviseeList = advisorMapper.findByProfessorId(professorId);
//
//        //리스트가 null이 아니고 비어있지 않으면 true 반환
//        return adviseeList != null && !adviseeList.isEmpty();
//    }
}