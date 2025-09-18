package com.ac.kr.academy.mapper.student;

import com.ac.kr.academy.domain.student.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Mapper
@Repository
public interface StudentMapper {
    /**
     * 학생의 고유 ID(Primary Key)로 학생 정보를 조회합니다.
     * @param id 학생 ID
     * @return Optional<Student> 객체. 해당 ID의 학생이 없으면 Optional.empty() 반환
     */
    Optional<Student> findById(Long id);

    /**
     * 학번으로 학생 정보를 조회합니다.
     * @param studentNum 학번
     * @return Optional<Student> 객체. 해당 학번의 학생이 없으면 Optional.empty() 반환
     */
    Optional<Student> findByStudentNum(String studentNum);

    /**
     * 사용자 ID로 학생 정보를 조회합니다.
     * @param userId 사용자 ID
     * @return Optional<Student> 객체. 해당 사용자 ID의 학생이 없으면 Optional.empty() 반환
     */
    Optional<Student> findByUserId(Long userId);
}
