<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>성적 등록</title>
</head>
<body>
<h2> 성적 등록 </h2>

<%-- 등록 처리 폼--%>
<form method="post" action="${pageContext.request.contextPath}/grade/professor/add">
    <input type="hidden" name="professorId" value="${param.professorId}" />

    <table border="1">
        <tr>
            <th>수강</th>
            <td><input type="number" name="enrollmentId" required></td>
        </tr>
        <tr>
            <th>과목ID</th>
            <td><input type="number" name="subjectId"></td>
        </tr>
        <tr>
            <th>학생ID</th>
            <td><input type="number" name="studentId"></td>
        </tr>

        <tr>
            <th>중간 점수</th>
            <td><input type="number" name="midScore" min="0" max="100" /></td>
        </tr>
        <tr>
            <th>기말 점수</th>
            <td><input type="number" name="finalScore" min="0" max="100" /></td>
        </tr>
        <tr>
            <th>중간 점수</th>
            <td><input type="number" name="assignmentScore" min="0" max="100" /></td>
        </tr>
        <tr>
            <th>중간 점수</th>
            <td><input type="number" name="attendanceScore" min="0" max="100" /></td>
        </tr>

        <tr>
            <th>등급</th>
            <td><input type="text" name="alphabet" placeholder="A+, A, B+, ..."/></td>
        </tr>
        <tr>
            <th>총점</th>
            <td><input type="number" name="score" min="0" max="100" /></td>
        </tr>
    </table>

    <div style="margin-top: 12px;">
        <button type="submit">등록</button>
        <a href="${pageContext.request.contextPath}/grade/professor/list?professorId=${param.professorId}">목록</a>
    </div>
</form>

</body>
</html>
