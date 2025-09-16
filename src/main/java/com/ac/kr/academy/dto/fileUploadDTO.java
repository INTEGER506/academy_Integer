package com.ac.kr.academy.dto;

import lombok.Data;

import java.sql.Date;

@Data
public class fileUploadDTO {
    private Long id;            // 파일 업로드 고유아이디 (UUID 등)
    private String fileName;    // 파일 원본명
    private String filePath;    // 파일 경로
    private long fileSize;      // 파일 크기 (byte)
    private Date uploadDate;    // 업로드날짜
}
