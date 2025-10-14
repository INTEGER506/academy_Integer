package com.ac.kr.academy.dto.page;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
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
    private int blockSize;

    // 블록
    private int totalPage;      // 전체 페이지 수
    private int startPage;      // 현재 페이지 블록의 시작 번호
    private int endPage;        // 형재 페이지 블록의 끝 번호
    private boolean hasNext;    // 다음 페이지가 있는지 여부
    private boolean hasPrevious;// 이전 페이지가 있는지 여부

    public void calculate() {
        int cp = (currentPage <= 0) ? 1 : currentPage;
        int ps = (pageSize <= 0) ? 10 : pageSize;
        int bs = (blockSize <= 0) ? 5 : blockSize;

        // 전체 페이지 수 계산 (올림)
        this.totalPage = Math.max(1, (int) Math.ceil((double) totalCount / ps));

        // 현재 페이지 보정 (마지막 페이지 초과 방지)
        if (cp > totalPage) cp = totalPage;
        this.currentPage = cp;

        // 블록 계산 (정수 나눗셈)
        int blockIndex = (cp - 1) / bs;        // 현재 페이지가 속한 블록 인덱스
        this.startPage = blockIndex * bs + 1;  // 시작 페이지
        this.endPage = Math.min(startPage + bs - 1, totalPage); // 끝 페이지

        // 이전/다음 블록 존재 여부
        this.hasPrevious = (startPage > 1);
        this.hasNext = (endPage < totalPage);

    }

    /* ===================== 공통 페이징 기능 ===================== */
    /**
     * 공통 페이징 DTO
     * - pageOf() : ServiceImpl에서 한 줄로 페이징 객체 만들기용
     * - 예시: return PageResponseDTO.pageOf(list, total, req);
     */

    public static <T> PageResponseDTO<T> pageOf(List<T> list, long total, PageRequestDTO req) {
        PageResponseDTO<T> resp = new PageResponseDTO<>();

        // null 방지
        resp.setData(list == null ? Collections.emptyList() : list);

        // 기본 세팅
        resp.setTotalCount((int) total);
        resp.setCurrentPage(req.getPage());
        resp.setPageSize(req.getPageSize());
        resp.setBlockSize(req.getBlockSize());

        // 블록 계산
        resp.calculate();

        return resp;
    }

    public void setData(List<T> list) {
        this.data = (list == null) ? new ArrayList<>() : list;
    }
}
