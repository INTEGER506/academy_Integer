package com.ac.kr.academy.dto.page;


import lombok.Data;

import java.util.List;

@Data
public class PageResponseDTO<T> {

    // 페이지 수
    private final long totalCount;    // 전체 게시글 수
    private final int totalPage;      // 전체 페이지 수
    private final int currentPage;    // 현재 페이지 번호 (1부터 시작)
    private final int pageSize;       // 총 게시물 수w
    private final List<T> data;       // 현재 페이지의 게시물 목록

    // 블록
    private final int startPage;      // 현재 페이지 블록의 시작 번호
    private final int endPage;        // 형재 페이지 블록의 끝 번호
    private final boolean hasNext;    // ekdma 페이지가 있는지 여부
    private final boolean hasPrevious;// 이전 페이지가 있는지 여부

    // 총 페이지 수 계산
    public PageResponseDTO(List<T> data, long totalCount, PageRequestDTO pageRequestDTO) {
        this.currentPage = pageRequestDTO.getPage();
        this.pageSize = pageRequestDTO.getPageSize();
        this.data = data;
        this.totalCount = totalCount;

        // 전체 페이지 수 계산 / 페이지당 계시물 수
        this.totalPage = (int)Math.ceil((double)totalCount / pageSize);
    }

}
