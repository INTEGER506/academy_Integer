<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>강좌 목록</title>
</head>
<body>
<h1>강좌 목록</h1>

<table border="1">
    <thead>
    <tr>
        <th>강좌 이름</th>
        <th>담당 교수</th>
        <th>학점</th>
        <th>수강 상태</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="course" items="${courseList}">
        <tr>
            <td>${course.subjectName}</td>
            <td>${course.professorName}</td>
            <td>${course.credit}</td>
            <td>
                <c:choose>
                    <c:when test="${enrolledCourseIds.contains(course.id)}">
                        <form action="/enrollments/cancel/${course.id}" method="post" style="display:inline;">
                            <button type="submit">취소</button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <form action="/enrollments/${course.id}" method="post" style="display:inline;">
                            <button type="submit">수강 신청</button>
                        </form>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

</body>
</html>
