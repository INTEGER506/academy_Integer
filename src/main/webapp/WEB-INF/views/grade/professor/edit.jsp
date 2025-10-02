<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>성적 수정</title>
</head>
<body>

<h2> 성적 수정 </h2>

<form method="post" action="${pageContext.request.contextPath}/grade/professor/edit">
    <input type="hidden" name="professorId" value="${param.professorId}"/>
    <input type="hidden" name="id" value="${grade.id}"/>

    <table border="1">
        <tr>
            <th>수강</th>
            <td><input type="number" name="enrollmentId" value="${grade.enrollmentId}" required/></td>
        </tr>
        <tr>
            <th>과목ID</th>
            <td><input type="number" name="subjectId" value="${grade.subjectId}"/></td>
        </tr>
        <tr>
            <th>학생ID</th>
            <td><input type="number" name="studentId" value="${grade.studentId}"/></td>
        </tr>

        <tr>
            <th>중간 점수</th>
            <td><input type="number" name="midScore" min="0" max="100" value="${grade.midScore}"/></td>
        </tr>
        <tr>
            <th>기말 점수</th>
            <td><input type="number" name="finalScore" min="0" max="100" value="${grade.finalScore}"/></td>
        </tr>
        <tr>
            <th>과제 점수</th>
            <td><input type="number" name="assignmentScore" min="0" max="100" value="${grade.assignmentScore}"/></td>
        </tr>
        <tr>
            <th>출석 점수</th>
            <td><input type="number" name="attendanceScore" min="0" max="100" value="${grade.attendanceScore}"/></td>
        </tr>

        <tr>
            <th>등급</th>
            <td><input type="text" name="alphabet" value="${grade.alphabet}"></td>
        </tr>
        <tr>
            <th>총점</th>
            <td><input type="number" name="score" min="0" max="100" value="${grade.score}"></td>
        </tr>
    </table>

    <div style="margin-top:12px;">
        <button type="submit">저장</button>
        <a href="${pageContext.request.contextPath}/grade/professor/list?professorId=${param.professorId}">목록</a>
    </div>
</form>

</body>
</html>
