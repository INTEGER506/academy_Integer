package com.ac.kr.academy.domain.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class Advisor {
    private Long id;
    private Long professorId;
    private Long studentId;
}
