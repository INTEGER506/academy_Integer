package com.ac.kr.academy.domain.user;

import lombok.Data;

import java.util.Date;

@Data
public class Staff {
    private Long id;
    private String staffNum;
    private Date createAt;
    private Date endedAt;
    private Long userId;
}
