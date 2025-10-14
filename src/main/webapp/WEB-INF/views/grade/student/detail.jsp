<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>성적 상세</title>
</head>
<body>

<h2>내 성적 상세</h2>

<%-- 상세 정보 테이블 --%>
<table border="1">
    <tr>
        <th>성적 ID</th>
        <td>${grade.id}</td>
    </tr>
    <tr>
        <th>과목명</th>
        <td>${grade.subjectName}</td>
    </tr>
    <tr>
        <th>등급</th>
        <td>${grade.alphabet}</td>
    </tr>
    <tr>
        <th>총점</th>
        <td>${grade.score}</td>
    </tr>

    <%--세부 점수--%>
    <tr>
        <th>중간 점수</th>
        <td>${grade.midExam}</td>
    </tr>
    <tr>
        <th>기말 점수</th>
        <td>${grade.finalExam}</td>
    </tr>
    <tr>
        <th>과제 점수</th>
        <td>${grade.assignment}</td>
    </tr>
    <tr>
        <th>출석 점수</th>
        <td>${grade.attendance}</td>
    </tr>
</table>

<div style="margin-top: 12px;">
    <%--목록으로--%>
    <a href="${pageContext.request.contextPath}/grade/student/list?studentId=${param.studentId}">목록</a>
</div>

</body>
</html>
