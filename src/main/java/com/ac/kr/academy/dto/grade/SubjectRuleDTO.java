package com.ac.kr.academy.dto.grade;

import lombok.Data;
import java.util.Map;

@Data
public class SubjectRuleDTO {
    private Long subjectId;
    private String subjectName;
    private Map<String, Double> percentages; // A+, A, B+, B, C+, C, D+, D, F
}
