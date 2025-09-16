package com.ac.kr.academy.controller.course;


import com.ac.kr.academy.dto.course.CourseCreateRequestDTO;
import com.ac.kr.academy.dto.course.CourseListResponseDTO;
import com.ac.kr.academy.dto.course.CourseUpdateRequestDTO;
import com.ac.kr.academy.service.course.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseRestController {

    private final CourseService courseService;

    //강의 개설
    @PostMapping("/add")
    public ResponseEntity<Void> addCourse(@RequestBody @Valid CourseCreateRequestDTO courseRequestDTO ) {
        Long userId = 1L; //실제 로그인한 사용자 ID로 대체해야함
        courseService.addCourse(courseRequestDTO, userId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    //강의 목록 조회
    @GetMapping
    public ResponseEntity<List<CourseListResponseDTO>> getCourseList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type) {

        List<CourseListResponseDTO> courseList = courseService.findAll(keyword, type);
        return new ResponseEntity<>(courseList, HttpStatus.OK);
    }

    //강의 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<CourseListResponseDTO> getCourseById(@PathVariable Long id) {
        CourseListResponseDTO course = courseService.findById(id);
        if ( course == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(course, HttpStatus.OK);
    }

    //강의 수정
    @PatchMapping("/course/{id}")
    public ResponseEntity<Void> updateCourse(@PathVariable Long id,
                                             @RequestBody @Valid CourseUpdateRequestDTO courseRequestDTO) {
        Long userId = 1L; //실제 로그인한 사용자 ID로 대체해야함
        courseService.update(courseRequestDTO, id, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //강의 삭제
    @DeleteMapping("/course/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        Long userId = 1L; //실제 로그인한 사용자 ID로 대체해야함
        courseService.delete(id, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
