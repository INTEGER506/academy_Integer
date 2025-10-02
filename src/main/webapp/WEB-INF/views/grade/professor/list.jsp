<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>교수 성적 목록</title>
</head>
<body>
<h2> 교수 성적 목록 </h2>

<%-- 검색바 공통 include --%>
<jsp:include page="/WEB-INF/views/common/searchBar.jsp">
    <%-- 검색 submit URL--%>
    <jsp:param name="formAction" value="${pageContext.request.contextPath}/grade/professor/list"/>
    <%-- 검색 placeholder --%>
    <jsp:param name="placeHolder" value="학생명/학번/과목명 검색"/>
    <%-- 유지할 hidden 값 (교수Id, 과목 Id 등) --%>
    <jsp:param name="keep"
               value='<input type="hidden" name="professorId" value="${param.professorId}"/>
                      <input type="hidden" name="courseId" value="${param.courseId}"/>
                      <input type="hidden" name="subjectId" value="${param.subjectId}"/>'
</jsp:include>

<%-- 목록 테이블 --%>

<table border="1" width="100%">
    <thead>
    <tr>
        <th>ID</th>
        <th>학생명</th>
        <th>학번</th>
        <th>과목명</th>
        <th>등급</th>
        <th>총점</th>
        <th>관리</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="g" items="${result.data}">
        <tr>
            <td>${g.id}</td>
            <td>${g.studentName}</td>
            <td>${g.studentNo}</td>
            <td>${g.subjectName}</td>
            <td>${g.alphabet}</td>
            <td>${g.score}</td>
            <td>
                <%-- 수정 --%>
                <a href="${pageContext.request.contextPath}/grade/professor/edit?id=${g.id}&professorId=${g.professorId}">수정</a>
                <%-- 삭제 폼 --%>
                <form method="post" action="${pageContext.request.contextPath}/grade/professor/delete" style="display: :inline;">
                    <input type="hidden" name="id" value="${g.id}"/>
                    <input type="hidden" name="professorId" value="${param.professorId}" />
                    <button type="submit" onclick="return confirm('삭제하시겠습니까?')">삭제</button>
                </form>
            </td>
        </tr>
    </c:forEach>

    <%-- 데이터 없을 경우 --%>
    <c:if test="${empty result.data}">
        <tr><td colspan="7">데이터가 없습니다</td> </tr>
    </c:if>
    </tbody>
</table>

<%-- 페이징 바 공통 include --%>
<jsp:include page="/WEB-INF/views/common/page.jsp">
    <jsp:param name="baseUrl" value="${pageContext.request.contextPath}/grade/professor/list"/>
    <jsp:param name="extraParams" value="&professorId=${param.professorId}&courseId=${param.courseId}&subjectId=${param.subjectId}&searchType=${req.searchType}&searchKeyword=${req.searchKeyword}"/>
</jsp:include>
</body>
</html>
