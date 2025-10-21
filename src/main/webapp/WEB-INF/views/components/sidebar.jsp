<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>


<aside class="sidebar card-white">
  <div class="menu-cap">MENU</div>
  <ul class="menu-list">

    <!-- ✅ 공통 메뉴 -->
    <li><a href="<c:url value='/calendar'/>">학사일정</a></li>    <%--병래--%>
    <li><a href="<c:url value='/notificationList'/>">알림센터</a></li>
    <li><a href="<c:url value='/notices'/>">공지사항</a></li>
    <li><a href="<c:url value='/mypage'/>">마이페이지</a></li>


    <!-- ✅ 관리자 메뉴 -->
  <sec:authorize access="hasRole('ROLE_ADMIN')">
    <li><a href="<c:url value='/auth/log-history'/>">접속 기록 관리</a></li>
    <li><a href="<c:url value='/auth/log-monitor'/>">로그 모니터링</a></li>
    <li><a href="<c:url value='/auth/user-list'/>">계정</a></li>
    <li><a href="<c:url value='/auth/advisor-role'/>">지도교수 지정</a></li>
    <li><a href="<c:url value='/grade/admin/rule/global'/>">성적 규정 관리</a></li>
</sec:authorize>

    <!-- ✅ 교수 메뉴 -->
   <sec:authorize access="hasRole('ROLE_PROFESSOR')">
      <li><a href="<c:url value='/professor-timetable'/>">내 강의 시간표</a></li> <%--병래--%>
      <li><a href="<c:url value='/grade/professor/courses?professorId=1'/>">성적 관리</a></li>
      <li><a href="<c:url value='/course-manage'/>">강좌 관리</a></li> <%--병래--%>
    </sec:authorize>

      <!-- ✅ 지도교수 메뉴 -->
   <sec:authorize access="hasRole('ROLE_ADVISOR')">
      <li><a href="<c:url value='/professor-timetable'/>">내 강의 시간표</a></li> <%--병래--%>
      <li><a href="<c:url value='/grade/professor/courses?professorId=1'/>">성적 관리</a></li>
      <li><a href="<c:url value='/course-manage'/>">강좌 관리</a></li> <%--병래--%>
      <li><a href="<c:url value='/graduation-review'/>">졸업 심사</a></li>
    </sec:authorize>

    <!-- ✅ 학생 메뉴 -->
    <sec:authorize access="hasRole('ROLE_STUDENT')">
      <li><a href="<c:url value='/enrollment'/>">수강 신청</a></li> <%--병래--%>
      <li><a href="<c:url value='/student-timetable'/>">내 시간표</a></li> <%--병래--%>
      <li><a href="<c:url value='/grade/student/1/semester-grades'/>">성적 조회</a></li>
      <li><a href="<c:url value='/graduation-status'/>">졸업 요건 조회</a></li>
    </sec:authorize>

  </ul>
</aside>
