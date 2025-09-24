package com.ac.kr.academy.dto.page;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponseDTO<T> {

    // 페이지 수
    private long totalCount;    // 전체 게시글 수
    private List<T> data;       // 현재 페이지의 게시물 목록

    private int currentPage;    // 현재 페이지 번호 (1부터 시작)
    private int pageSize;       // 총 게시물 수

    // 블록
    private int totalPage;      // 전체 페이지 수
    private int startPage;      // 현재 페이지 블록의 시작 번호
    private int endPage;        // 형재 페이지 블록의 끝 번호
    private boolean hasNext;    // 다음 페이지가 있는지 여부
    private boolean hasPrevious;// 이전 페이지가 있는지 여부

    // 총 페이지 수 계산
    public PageResponseDTO(List<T> data, long totalCount, int currentPage, int pageSize) {
        // 데이터 NULL 방지
        this.data = (data == null) ? new ArrayList<>() : data;

        // 카운트/ 현재페이지/ 사이즈 세팅
        this.totalCount = totalCount;
        this.currentPage = currentPage;
        this.pageSize = pageSize;

        // 페이지 블록 계산 (10으로 가정)
        int blockSize = 10;

        // 블록 끝 페이지 계산
        int tempEnd = (int) Math.ceil((double) this.currentPage / blockSize);

        // 전체 페이지 수
        this.totalPage = (int) Math.ceil((double) this.totalCount / (double) pageSize);

        // 이전/ 다음 블록 존재 여부
        this.hasPrevious = this.startPage > 1;
        this.hasNext = this.endPage < this.totalPage;
    }

}
