<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>점수 분배 목록</title>
</head>
<body>

<h2> 점수분배 목록 </h2>

<%-- 페이지 크기 선택 (검색바 X)--%>
<form method="get" action="${pageContext.request.contextPath}/grade/professor/system/list">
    <input type="hidden" name="courseId" value="${param.courseId}" />
    <select name="pageSize" onchange="this.form.submit()">
        <option value="5" <c:if test="${req.pageSize == 5}">selected</c:if>>5개</option>
        <option value="10" <c:if test="${req.pageSize == 10}">selected</c:if>>10개</option>
        <option value="15" <c:if test="${req.pageSize == 15}">selected</c:if>>15개</option>
    </select>
</form>

<%-- 목록 테이블 --%>
<table border="1" width="100%">
    <thead>
    <tr>
        <th>ID</th>
        <th>중간%</th>
        <th>기말%</th>
        <th>과제%</th>
        <th>출석%</th>
        <th>관리</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="s" items="${result.data}">
        <tr>
            <td>${s.id}</td>
            <td>${s.midExamRatio}</td>
            <td>${s.finalExamRatio}</td>
            <td>${s.assignmentRatio}</td>
            <td>${s.attendanceRatio}</td>
            <td>
                <%-- 수정 폼 : 저장하면 Contoroller로 POST--%>
                <form method="post" action="${pageContext.request.contextPath}/grade/professor/system/edit" style="display: :inline;">
                    <input type="hidden" name="courseId" value="${param.courseId}" />
                    <input type="hidden" name="id" value="${s.id}">

                    <%-- 현재 값으로 바로 수정 가능--%>
                    <input type="number" name="midExamRatio" value="${s.midExamRatio}" min="0" max="100" />
                    <input type="number" name="finalExamRatio" value="${s.finalExamRatio}" min="0" max="100" />
                    <input type="number" name="assignmentRatio" value="${s.assignmentRatio}" min="0" max="100" />
                    <input type="number" name="attendanceRatio" value="${s.attendanceRatio}" min="0" max="100" />
                    <button type="submit">저장</button>
                </form>
            </td>
        </tr>
    </c:forEach>

    <%-- 데이터 없을 경우 --%>
    <c:if test="${empty result.data}">
        <tr><td colspan="6">데이터 없음</td> </tr>
    </c:if>
    </tbody>
</table>

<!-- ================= 페이징 바 공통 include ================= -->
<jsp:include page="/WEB-INF/views/common/page.jsp">
    <jsp:param name="baseUrl" value="${pageContext.request.contextPath}/grade/professor/system/list" />
    <jsp:param name="extraParams" value="&courseId=${param.courseId}" />
</jsp:include>
</body>
</html>
