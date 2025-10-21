package com.ac.kr.academy.domain.calendar;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Calendar {
    private Long id;
    private String content;
    private LocalDate startDate;
    private LocalDate endDate;
    private String color;
    private Long userId;
}
