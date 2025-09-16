<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>수강 신청</title>
</head>
<body>
<h1>수강 신청 가능 강의 목록</h1>

<c:if test="${not empty message}">
    <div>${message}</div>
</c:if>
<c:if test="${not empty error}">
    <div>${error}</div>
</c:if>

<c:if test="${not empty courses}">
    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>과목명</th>
            <th>교수명</th>
            <th>학점</th>
            <th>요일</th>
            <th>시간</th>
            <th>장소</th>
            <th>현재 수강인원</th>
            <th>정원</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="course" items="${courses}">
            <tr>
                <td>${course.id}</td>
                <td><a href="/enrollments/${course.id}">${course.subjectName}</a></td>
                <td>${course.professorName}</td>
                <td>${course.credit}</td>
                <td>${course.dayOfWeek}</td>
                <td>${course.time}</td>
                <td>${course.place}</td>
                <td>${course.numOfStudent}</td>
                <td>${course.capacity}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</c:if>
<c:if test="${empty courses}">
    <p>수강 신청 가능한 강의가 없습니다.</p>
</c:if>

</body>
</html>
