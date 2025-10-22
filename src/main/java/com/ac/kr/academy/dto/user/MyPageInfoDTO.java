package com.ac.kr.academy.dto.user;

import lombok.Data;

import java.time.LocalDate;

/** 마이페이지 정보 조회 및 수정을 위한 DTO
 * 도메인 객체는 건들지 않고, JOIN 결과인 deptName 필드를 포함하는 새로운 데이터 전송용 객체
 * */

@Data
public class MyPageInfoDTO {
    //공통 필드
    private Long id;
    private Long deptId;
    private Long userId;
    private LocalDate createdAt;
    private LocalDate endedAt;

    //join 필요
    private String deptName;

    //학생일 경우
    private String studentNum;

    //교수일 경우
    private String professorNum;

    //교직원일 경우
    private String staffNum;

    //학생 전용 필드
    private String status;              //재학상태
    private Integer enrolledCredits;    //수강 신청한 총 학점

    //마이페이지 사용자 수정
    private String email;
    private String phone;
    private String name;        //조회용 (수정불가)
    private String username;    //조회용 (수정불가)
    private String role;        //조회용
}