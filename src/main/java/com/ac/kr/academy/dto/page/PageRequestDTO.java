package com.ac.kr.academy.dto.page;

import lombok.Data;

@Data
public class PageRequestDTO {
    private int page;               // 현재 페이지
    private int pageSize;           // 페이지 당 보여질 게시물 수
    private String searchKeyword;   // 검색 키워드
    private String searchType;      // 검색 조건 (ex: 작성자, 제목, 내용 등)


    // 페이징 숫자 0이하 불가능하게 검사
    public int getPage() {
        return page <= 0 ? 1 : page;
    }

    // 페이지 시작 번호
    public int getStartRow() {
        return (page - 1) * pageSize + 1;
    }

    // 페이지 마지막 번호
    public int getEndRow() {
        return (page - 1) * pageSize - 1;
    }

    //각 기능들(알림,공지사항 등)에 넣어서 원하는 게시물 수 지정 [Controller에 넣을것]
/*  예시
    알림 페이지
    @GetMapping("/notice")
    public String getNoticeList(@RequestParam int page, Model model) {

        // 1. PagingRequest 객체를 생성
        PagingRequest pagingRequest = new PagingRequest();

        // 2. 현재 페이지 번호 설정
        pagingRequest.setPage(page);

        // 3. 알림은 페이지당 5개로 설정
        pagingRequest.setPageSize(5);

        // 4. 서비스 호출
        List<Notice> noticeList = boardService.getNoticeList(pagingRequest);

        // 5. Model에 담아서 View로 전달
        model.addAttribute("list", noticeList);

        return "notice/list"; // Notice/list.jsp
    }

    // pagingRequest.setPageSize()의 값만 바꾸면 됌
}
*/
}
