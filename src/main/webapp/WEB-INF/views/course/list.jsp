<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>강의 목록</title>
</head>
<body>
<h1>강의 목록</h1>
<a href="/courses/add">새 강의 개설</a>
<table border="1">
    <tr>
        <th>ID</th>
        <th>강의명</th>
        <th>정원</th>
        <th>관리</th>
    </tr>
    <c:forEach var="course" items="${courses}">
        <tr>
            <td><c:out value="${course.courseId}" /></td>
            <td><c:out value="${course.subject.name}" /></td>
            <td><c:out value="${course.capacity}" /></td>
            <td>
                <a href="/courses/edit/${course.courseId}">수정</a>
                <a href="/courses/delete/${course.courseId}">삭제</a>
            </td>
        </tr>
    </c:forEach>
</table>
</body>
</html>