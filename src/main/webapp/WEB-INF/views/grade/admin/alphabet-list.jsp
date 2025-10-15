<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>글로벌 성적 규정</h2>

<jsp:include page="/WEB-INF/views/common/searchBar.jsp">
    <jsp:param name="formAction"      value="${pageContext.request.contextPath}/grade/admin/alphabet-list"/>
    <jsp:param name="optionValues"    value="alphabet|boundary"/>
    <jsp:param name="optionLabels"    value="학점문자|경계값"/>
    <jsp:param name="pageSizeOptions" value="10|20|50"/>
    <jsp:param name="placeHolder"     value="A+,A,B+,B 또는 경계값 숫자"/>
    <jsp:param name="keep"            value=""/>
    <jsp:param name="req"             value="${req}"/>
</jsp:include>

<table class="table">
    <thead>
    <tr>
        <th>#</th>
        <th>학점</th>
        <th>경계값</th>
        <th>관리</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="row" items="${result.data}" varStatus="st">
        <tr>
            <td>${result.startPage + st.index + 1}</td>
            <td>${row.alphabet}</td>
            <td>${row.boundary}</td>
            <td>
                <a href="${pageContext.request.contextPath}/grade/admin/alphabet/edit?id=${row.id}">수정</a>
                |
                <a href="${pageContext.request.contextPath}/grade/admin/alphabet/delete?id=${row.id}"
                   onclick="return confirm('삭제할까요?');">삭제</a>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty result.data}">
        <tr><td colspan="4">데이터가 없습니다.</td></tr>
    </c:if>
    </tbody>
</table>

<div class="mt-2">
    <a class="btn" href="${pageContext.request.contextPath}/grade/admin/alphabet/add">+ 규정 추가</a>
</div>

<jsp:include page="/WEB-INF/views/common/page.jsp">
    <jsp:param name="baseUrl"    value="${pageContext.request.contextPath}/grade/admin/alphabet-list"/>
    <jsp:param name="req"        value="${req}"/>
    <jsp:param name="result"     value="${result}"/>
    <jsp:param name="keepParams" value="${keepParams}"/>
</jsp:include>