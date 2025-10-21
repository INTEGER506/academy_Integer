package com.ac.kr.academy.service.user;

public interface StudentService {

    /**
     * 학생의 현재 재학 상태(예: '재학', '휴학', '제적')를 조회합니다.
     * 수강 신청 가능 여부를 확인하는 데 사용됩니다.
     */
    String findStudentStatus(Long userId);

    /**
     * 강의 폐강 시 학생에게 학점을 반환하고 관련 학점을 업데이트합니다.
     * 이 메서드는 학생의 '총 신청 학점'을 감소시키는 등의 트랜잭션을 처리합니다.
     */
    void returnCredit(Long userId, int credit);

}
