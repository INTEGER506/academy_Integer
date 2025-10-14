<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>내 성적 목록</title>
</head>
<body>

<h2> 내 성적 목록 </h2>

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
        <th>상세</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="g" items="${result.data}">
        <tr>
            <td>${g.id}</td>
            <td>${g.subjectName}</td>
            <td>${g.alphabet}</td>
            <td>${g.score}</td>
            <td>
                <a href="${pageContext.request.contextPath}/grade/student/${studentId}/detail/${g.id}">보기</a>
            </td>
        </tr>
    </c:forEach>

    <%-- 데이터 없을 때 --%>
    <c:if test="${empty result.data}">
        <tr>
            <td colspan="5">데이터 없음</td>
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
