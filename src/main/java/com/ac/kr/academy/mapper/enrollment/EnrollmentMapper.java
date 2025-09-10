package com.ac.kr.academy.mapper.enrollment;


import com.ac.kr.academy.domain.enrollment.Enrollment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface EnrollmentMapper {

    void save(Enrollment enrollment);

    Optional<Enrollment> findByCourseIdAndStudentId(@Param("courseId") Long courseId,
                                                    @Param("studentId") Long studentId);

    void delete(@Param("courseId") Long  courseId, @Param("studentId") Long studentId);

    int countStudentsByCourseId(Long courseId);
}
