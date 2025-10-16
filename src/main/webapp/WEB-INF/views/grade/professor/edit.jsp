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
            <td><input type="number" name="midExam" min="0" max="100" value="${grade.midExam}"/></td>
        </tr>
        <tr>
            <th>기말 점수</th>
            <td><input type="number" name="finalExam" min="0" max="100" value="${grade.finalExam}"/></td>
        </tr>
        <tr>
            <th>과제 점수</th>
            <td><input type="number" name="assignment" min="0" max="100" value="${grade.assignment}"/></td>
        </tr>
        <tr>
            <th>출석 점수</th>
            <td><input type="number" name="attendance" min="0" max="100" value="${grade.attendance}"/></td>
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
        <button type="button" onclick="previewGrade()">미리보기</button>
        <span id="previewArea" style="margin-left:8px;color:#333"></span>
        <button type="submit">저장</button>
        <a href="${pageContext.request.contextPath}/grade/professor/list?professorId=${param.professorId}">목록</a>
    </div>
    
    <div style="margin-top: 15px; padding: 10px; background-color: #f8f9fa; border-radius: 4px;">
        <strong>📊 성적 규정 확인:</strong>
        <a href="${pageContext.request.contextPath}/grade/professor/rule/global" target="_blank" style="margin-left: 10px;">글로벌 규정 보기</a>
        <a href="${pageContext.request.contextPath}/grade/professor/rule/subject/${param.subjectId}" target="_blank" style="margin-left: 10px;">과목별 규정 보기</a>
    </div>
</form>

<script>
    async function previewGrade(){
        const body = {
            enrollmentId: Number(document.querySelector('input[name="enrollmentId"]').value),
            midExam: Number(document.querySelector('input[name="midExam"]').value||0),
            finalExam: Number(document.querySelector('input[name="finalExam"]').value||0),
            assignment: Number(document.querySelector('input[name="assignment"]').value||0),
            attendance: Number(document.querySelector('input[name="attendance"]').value||0)
        };
        const res = await fetch(`${location.origin}/api/grade/preview`,{
            method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify(body)
        });
        if(!res.ok){ document.getElementById('previewArea').textContent='미리보기 실패'; return; }
        const data = await res.json();
        const gpa = (data.gpa10/10).toFixed(1);
        document.getElementById('previewArea').textContent = `총점 ${data.totalScore}, 학점 ${data.alphabet}, GPA ${gpa}`;
    }
</script>

</body>
</html>
