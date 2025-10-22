<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>


<aside class="sidebar card-white">
  <div class="menu-cap">MENU</div>
  <ul class="menu-list">

    <!--  공통 메뉴 -->
    <li><a href="<c:url value='/calendar'/>">학사일정</a></li>
    <li><a href="<c:url value='/notificationList'/>">알림센터</a></li>
    <li><a href="<c:url value='/notices'/>">공지사항</a></li>
    <li><a href="<c:url value='/mypage'/>">마이페이지</a></li>

    <!--  관리자 메뉴 -->
  <sec:authorize access="hasRole('ROLE_ADMIN')">
  <li><a href="<c:url value='/courses'/>">강의 목록</a></li>
  <li><a href="<c:url value='/semester'/>">학기 관리</a></li>
    <li><a href="<c:url value='/auth/log-history'/>">접속 기록 관리</a></li>
    <li><a href="<c:url value='/auth/log-monitor'/>">로그 모니터링</a></li>
    <li><a href="<c:url value='/auth/user-list'/>">계정</a></li>
    <li><a href="<c:url value='/auth/advisor-role'/>">지도교수 지정</a></li>
    <li><a href="<c:url value='/grade/admin/rule-global'/>">전체 규정 설정</a></li>
    <li><a href="<c:url value='/grade/admin/subject-rules'/>">과목별 규정 설정</a></li>
</sec:authorize>

    <!--  교수 메뉴 -->
   <sec:authorize access="hasRole('ROLE_PROFESSOR')">
      <li><a href="<c:url value='/timetable/professor'/>">내 강의 시간표</a></li>
      <li><a href="<c:url value='/grade/professor/courses'/>">성적 관리</a></li>
      <li><a href="<c:url value='/grade/professor/system-list'/>">점수 분배 설정</a></li>
      <li><a href="<c:url value='/courses'/>">강의 목록</a></li>
      <li><a href="<c:url value='/courses/add'/>">강의 개설</a></li>
    </sec:authorize>

      <!--  지도교수 메뉴 -->
      <sec:authorize access="hasRole('ROLE_ADVISOR')">

        <li><a href="<c:url value='/courses'/>">강의 목록</a></li>
      </sec:authorize>

    <!--  학생 메뉴 -->
    <sec:authorize access="hasRole('ROLE_STUDENT')">
      <li><a href="<c:url value='/enrollments'/>">수강 신청</a></li>
      <li><a href="<c:url value='/enrollments/my-courses'/>">수강 목록</a></li>
      <li><a href="<c:url value='/timetable/student'/>">내 시간표</a></li>
      <li><a href="<c:url value='/grade/student/semester-grades'/>">성적 조회</a></li>
    </sec:authorize>
  </ul>
</aside>
