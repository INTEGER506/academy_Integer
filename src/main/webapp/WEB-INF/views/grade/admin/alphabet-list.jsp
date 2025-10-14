<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <title>점수 규정 목록</title>
</head>
<body>
<h2>점수 규정 목록</h2>


<jsp:include page="/WEB-INF/views/common/searchBar.jsp">
    <jsp:param name="formAction"      value="${pageContext.request.contextPath}/grade/admin/rule/global"/>
    <jsp:param name="placeHolder"     value="규정명 검색"/>
    <jsp:param name="optionValues"    value="alphabet|boundary"/>
    <jsp:param name="optionLabels"    value="등급|경계값"/>
    <jsp:param name="pageSizeOptions" value="10|20|30"/>
    <jsp:param name="keep"            value=""/>
</jsp:include>

<%--등록 버튼--%>
<a href="${pageContext.request.contextPath}/grade/admin/alphabet/add">점수 규정 추가</a>

<table border="1" width="100%">
    <thead>
    <tr>
        <th>ID</th>
        <th>등급</th>
        <th>최소 점수</th>
        <th>최대 점수</th>
        <th>비율(%)</th>
        <th>관리</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="a" items="${result.data}">
        <tr>
            <td>${a.id}</td>
            <td>${a.alphabet}</td>
            <td>${a.minScore}</td>
            <td>${a.maxScore}</td>
            <td>${a.ratio}</td>
            <td><a href="${pageContext.request.contextPath}/grade/admin/alphabet/edit?id=${a.id}">수정</a>
            <form method="post" action="${pageContext.request.contextPath}/grade/admin/alphabet/delete" style="display: inline;">
                <input type="hidden" name="id" value="${a.id}" />
                <button type="submit" onclick="return confirm('삭제하시겠습니까?')">삭제</button>
            </form>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty result.data}">
        <tr><td colspan="6">데이터 없음</td> </tr>
    </c:if>
    </tbody>
</table>

<!-- 공통 페이징 include -->
<jsp:include page="/WEB-INF/views/common/page.jsp">
    <jsp:param name="baseUrl" value="${pageContext.request.contextPath}/grade/admin/rule/global"/>
    <jsp:param name="extraParams" value="" />
</jsp:include>
</body>
</html>
