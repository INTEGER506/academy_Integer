<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>점수 비율(코스별)</h2>

<jsp:include page="/WEB-INF/views/common/searchBar.jsp">
    <jsp:param name="formAction"      value="${pageContext.request.contextPath}/grade/professor/system-list"/>
    <jsp:param name="optionValues"    value="courseName"/>
    <jsp:param name="optionLabels"    value="코스명"/>
    <jsp:param name="pageSizeOptions" value="10|20|50"/>
    <jsp:param name="placeHolder"     value="코스명 검색"/>
    <jsp:param name="keep"            value=""/>
    <jsp:param name="req"             value="${req}"/>
</jsp:include>

<table class="table">
    <thead>
    <tr>
        <th>#</th>
        <th>코스</th>
        <th>중간%</th>
        <th>기말%</th>
        <th>과제%</th>
        <th>출석%</th>
        <th>관리</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="gs" items="${result.list}" varStatus="st">
        <tr>
            <td>${result.start + st.index + 1}</td>
            <td>${gs.courseName}</td>
            <td>${gs.midExamRatio}</td>
            <td>${gs.finalExamRatio}</td>
            <td>${gs.assignmentRatio}</td>
            <td>${gs.attendanceRatio}</td>
            <td>
                <a href="${pageContext.request.contextPath}/grade/professor/system/edit?id=${gs.id}">수정</a>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty result.list}">
        <tr><td colspan="7">데이터가 없습니다.</td></tr>
    </c:if>
    </tbody>
</table>

<jsp:include page="/WEB-INF/views/common/page.jsp">
    <jsp:param name="baseUrl"    value="${pageContext.request.contextPath}/grade/professor/system-list"/>
    <jsp:param name="req"        value="${req}"/>
    <jsp:param name="result"     value="${result}"/>
    <jsp:param name="keepParams" value="${keepParams}"/>
</jsp:include>