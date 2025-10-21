package com.ac.kr.academy.domain.enrollment;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Enrollment {

    private Long id;
    private Long courseId;
    private Long studentId;
}
