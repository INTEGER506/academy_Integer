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
    private int blockSize;      // 블록사이즈

    // 요청 DTO를 받아 page/pageSize 기준으로 totalPages/prev/next 계산
    public PageResponseDTO(List<T> data, int currentPage, int pageSize, long totalCount, int blockSize) {
        this.data = data;
        this.totalCount = totalCount;
        this.pageSize = pageSize <= 0 ? 10 : pageSize;

        //전체 페이지 수 계산
        this.totalPage = (int) Math.ceil((double) this.totalCount / pageSize);
        //현재 페이지 보정: 1 미만이면 1
        this.currentPage = currentPage <= 0 ? 1 : currentPage;


        // 페이지블록
        int bs = (blockSize <= 0 ? 5 : blockSize);                             // 기본 블록 크기 5
        int blockIndex = (this.currentPage - 1) * bs;                          // 현재 페이지가 속한 블록 인덱스
        this.startPage = blockIndex * bs + 1;                                  // 블록 시작 페이지 번호
        this.endPage = Math.min(this.startPage + bs -1, this.totalPage);       // 블록 끝 페이지 번호

        this.hasPrevious = (this.currentPage > 1);                             // 이전 페이지 존재 여부(1보다 크면 이전페이지 있음)
        this.hasNext = (this.currentPage < this.endPage);                      // 다음 페이지 존재 여부(마지막보다 작으면 다음페이지 있음)
    }

}
