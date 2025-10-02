package com.ac.kr.academy.dto.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageRequestDTO {

    /*================= 페이징 =================*/
    //컨트롤러에서 값변경 세팅 권장
    private int page = 1;                // 현재 페이지
    private int pageSize = 10;           // 페이지 당 보여질 게시물 수

    /*================= 검색 ==================*/
    private String searchKeyword;   // 검색 키워드
    private String searchType;      // 검색 조건 (ex: 작성자, 제목, 내용 등)

    // 검색 키워드 존재 여부 / null이나 공백이면 false

    // 페이징 숫자 0이하 불가능하게 검사
    public int getPage() {
        return page <= 0 ? 1 : page;
    }

    //
    public int getPageSize() {
        return pageSize <= 0 ? 10 : pageSize;
    }

    // 페이지 시작 번호
    public int getStart() {
        int p  = (page <= 0) ? 1  : page;
        int ps = (pageSize <= 0) ? 10 : pageSize;
        return (p - 1) * ps;
    }

    // 페이지 마지막 번호
    public int getEnd() {
        int p  = (page <= 0) ? 1  : page;
        int ps = (pageSize <= 0) ? 10 : pageSize;
        return p * ps;
    }
}
