package com.ac.kr.academy.dto.enrollment;


import com.ac.kr.academy.dto.course.CourseListResponseDTO;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class EnrollmentListResponseDTO extends CourseListResponseDTO {

    private Long enrollmentId;

    // 학생의 해당 강의 신청 상태
    private String applicationStatus; // NONE, APPLIED, WAITNG

    // 수강신청 기간에 따라 신청 가능 여부
    private boolean canEnroll; // true면 신청 버튼 활성화, false면 비활성화
}
