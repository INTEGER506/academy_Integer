package com.ac.kr.academy.mapper.user.student;

import com.ac.kr.academy.domain.user.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentMapper {

    void insertStudent(Student student);
    int updateStudent(Student student);
    int deleteStudent(@Param("id") Long id);

    List<Student> findAllStudents();
    Student findById(@Param("id") Long id);
    Student findStudentNum(@Param("studentNum") String studentNum);

}
