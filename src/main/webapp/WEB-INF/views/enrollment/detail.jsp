<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>강의 상세</title>
</head>
<body>
<h1>${course.subjectName} 상세 정보</h1>

<c:if test="${not empty message}">
    <div>${message}</div>
</c:if>
<c:if test="${not empty error}">
    <div>${error}</div>
</c:if>

<ul>
    <li>교수명: ${course.professorName}</li>
    <li>학점: ${course.credit}</li>
    <li>요일: ${course.dayOfWeek}</li>
    <li>시간: ${course.time}</li>
    <li>장소: ${course.place}</li>
    <li>현재 수강인원: ${course.numOfStudent}명</li>
    <li>정원: ${course.capacity}명</li>
</ul>

<form action="/enrollments/${course.id}" method="post">
    <button type="submit">수강 신청</button>
</form>

<button onclick="history.back()">뒤로가기</button>

</body>
</html>
