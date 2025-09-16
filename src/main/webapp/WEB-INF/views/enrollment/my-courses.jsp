<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>내 수강 목록</title>
</head>
<body>
<h1>내 수강 목록</h1>

<c:if test="${not empty myCourses}">
  <table>
    <thead>
    <tr>
      <th>ID</th>
      <th>과목명</th>
      <th>교수명</th>
      <th>요일</th>
      <th>시간</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="course" items="${myCourses}">
      <tr>
        <td>${course.id}</td>
        <td>${course.subjectName}</td>
        <td>${course.professorName}</td>
        <td>${course.dayOfWeek}</td>
        <td>${course.time}</td>
      </tr>
    </c:forEach>
    </tbody>
  </table>
</c:if>
<c:if test="${empty myCourses}">
  <p>수강 중인 강의가 없습니다.</p>
</c:if>

</body>
</html>
