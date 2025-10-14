<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>내 수강 목록</title>
</head>
<body>
<h1>내가 신청한 강의 목록</h1>

<table border="1">
  <thead>
  <tr>
    <th>강의번호</th>
    <th>과목명</th>
    <th>요일</th>
    <th>시간</th>
    <th>강의실</th>
    <th>학점</th>
  </tr>
  </thead>
  <tbody>
  <c:forEach var="course" items="${myCourses}">
    <tr>
      <td>${course.id}</td>
      <td>${course.subjectName}</td>
      <td>${course.dayOfWeek}</td>
      <td>${course.time}</td>
      <td>${course.place}</td>
      <td>${course.credit}</td>
      <td>
        <form action="/enrollments/cancel/${course.id}" method="post">
          <button type="submit">취소</button>
        </form>
      </td>
    </tr>
  </c:forEach>
  </tbody>
</table>

</body>
</html>
