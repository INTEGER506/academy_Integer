package com.ac.kr.academy.domain.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Data
public class Staff {
    private Long id;
    private String staffNum;
    private LocalDate createdAt;
    private LocalDate endedAt;
    private Long userId;
}
