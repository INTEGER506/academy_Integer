<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>내 성적 목록</title>
</head>
<body>

<h2> 내 성적 목록 </h2>

<div id="summary" style="margin:8px 0; padding:8px; border:1px solid #ddd;">
    총 취득학점: <span id="sumScore">-</span> | 평균 GPA: <span id="avgGpa">-</span>
    <span id="gradCheck" style="margin-left:8px;"></span>
    <button type="button" onclick="loadSummary()">새로고침</button>
 </div>

<%-- 검색 바 공통 Include --%>
<%-- 유지할 hidden 값 (학생ID) --%>
<%-- 검색 placeholder--%>
<%--검색 submit URL --%>
<jsp:include page="/WEB-INF/views/common/searchBar.jsp">
    <jsp:param name="formAction" value="${pageContext.request.contextPath}/grade/student/${studentId}/list"/>
    <jsp:param name="keep"
               value="<input type='hidden' name='studentId' value='${studentId}'/>"/>
    <jsp:param name="placeHolder" value="전체 | 학번 | 과목명 | 등급 "/>
    <jsp:param name="optionValues" value="all | id | subject | alphabet"/>
    <jsp:param name="optionLabels" value="전체 | 학번 | 과목명 | 등급"/>

    <jsp:param name="pageSizeOptions" value="5|10|20"/>
</jsp:include>

<%-- 목록 테이블 --%>
<table border="1" width="100%">
    <thead>
    <tr>
        <th>학번</th>
        <th>과목명</th>
        <th>등급</th>
        <th>총점</th>
        <th>취득학점</th>
        <th>GPA</th>
        <th>상세</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="g" items="${result.data}">
        <tr>
            <td>${g.studentNo}</td>
            <td>${g.subjectName}</td>
            <td>${g.alphabet}</td>
            <td>${g.totalInt}</td>
            <td>${g.score}</td>
            <td><c:out value="${g.gpa}"/></td>
            <td>
                <a href="${pageContext.request.contextPath}/grade/student/${studentId}/detail/${g.id}">보기</a>
            </td>
        </tr>
    </c:forEach>

    <%-- 데이터 없을 때 --%>
    <c:if test="${empty result.data}">
        <tr>
            <td colspan="7">데이터 없음</td>
        </tr>
    </c:if>
    </tbody>
</table>

<%-- 페이징 바 공통 include--%>
<jsp:include page="/WEB-INF/views/common/page.jsp">
    <jsp:param name="baseUrl" value="${pageContext.request.contextPath}/grade/student/${studentId}/list"/>
</jsp:include>

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
