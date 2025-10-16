<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>성적 상세</title>
</head>
<body>

<h2>내 성적 상세</h2>

<div id="summary" style="margin:8px 0; padding:8px; border:1px solid #ddd;">
    총 취득학점: <span id="sumScore">-</span> | 평균 GPA: <span id="avgGpa">-</span>
    <span id="gradCheck" style="margin-left:8px;"></span>
 </div>

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
        <td>${grade.totalInt}</td>
    </tr>
    <tr>
        <th>취득학점</th>
        <td>${grade.score}</td>
    </tr>
    <tr>
        <th>GPA</th>
        <td><c:out value="${grade.gpa}"/></td>
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
        <a href="${pageContext.request.contextPath}/grade/student/${studentId}/list">목록</a>
</div>

</body>
</html>

<script>
    async function loadSummary(){
        const base = `${location.origin}/api/grade/student/${'${studentId}'}`;
        const s = await fetch(base + '/summary');
        if(s.ok){ const d = await s.json();
            document.getElementById('sumScore').textContent = d.totalScore;
            document.getElementById('avgGpa').textContent = (d.avgGpa||0).toFixed(1);
        }
        const g = await fetch(base + '/graduation?requiredScore=130&requiredAvgGpa=2.5');
        if(g.ok){ const d = await g.json();
            document.getElementById('gradCheck').textContent = d.meets ? '(졸업요건 충족)' : '(졸업요건 미충족)';
        }
    }
    loadSummary();
</script>
