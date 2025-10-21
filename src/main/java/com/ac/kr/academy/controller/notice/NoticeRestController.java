package com.ac.kr.academy.controller.notice;

import com.ac.kr.academy.domain.notice.Notice;
import com.ac.kr.academy.service.notice.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
public class NoticeRestController {
    private final NoticeService noticeService;

    // 전체 조회
    @GetMapping
    public ResponseEntity<List<Notice>> getNoticeList(){
        return ResponseEntity.ok(noticeService.getNoticeList());
    }

    // 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<Notice> getNotice(@PathVariable Long id){
        Notice notice = noticeService.getNoticeDetail(id);
        return notice != null ? ResponseEntity.ok(notice) : ResponseEntity.notFound().build();
    }

    // 생성
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Notice> addNotice(@RequestBody Notice notice){
        noticeService.createNotice(notice);
        return ResponseEntity.status(201).body(notice);
    }

    // 수정
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Notice> editNotice(@PathVariable Long id, @RequestBody Notice notice){
        notice.setId(id);
        Notice updated = noticeService.editNotice(notice);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    // 삭제
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeNotice(@PathVariable Long id){
        return noticeService.removeNotice(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
