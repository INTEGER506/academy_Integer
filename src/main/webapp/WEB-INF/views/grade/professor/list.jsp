<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>성적 목록</h2>

<jsp:include page="/WEB-INF/views/common/searchBar.jsp">
    <jsp:param name="formAction"      value="${pageContext.request.contextPath}/grade/professor/list"/>
    <jsp:param name="optionValues"    value="studentName|studentNo|subjectName|alphabet"/>
    <jsp:param name="optionLabels"    value="학생명|학번|과목명|학점"/>
    <jsp:param name="pageSizeOptions" value="10|20|50"/>
    <jsp:param name="placeHolder"     value="학생명/학번/과목명/학점"/>
    <jsp:param name="keep"            value="courseId=${param.courseId}&subjectId=${param.subjectId}"/>
    <jsp:param name="req"             value="${req}"/>
</jsp:include>

<table class="table">
    <thead>
    <tr>
        <th>#</th>
        <th>학생</th>
        <th>학번</th>
        <th>과목</th>
        <th>중간</th>
        <th>기말</th>
        <th>과제</th>
        <th>출석</th>
        <th>총점</th>
        <th>학점</th>
        <th>GPA</th>
        <th>관리</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="g" items="${result.list}" varStatus="st">
        <tr>
            <td>${result.start + st.index + 1}</td>
            <td>${g.studentName}</td>
            <td>${g.studentNo}</td>
            <td>${g.subjectName}</td>
            <td>${g.midExam}</td>
            <td>${g.finalExam}</td>
            <td>${g.assignment}</td>
            <td>${g.attendance}</td>
            <td>${g.totalInt}</td>
            <td>${g.alphabet}</td>
            <td><c:out value="${g.gpa}"/></td>
            <td>
                <a href="${pageContext.request.contextPath}/grade/professor/edit?id=${g.id}">수정</a>
                |
                <a href="${pageContext.request.contextPath}/grade/professor/delete?id=${g.id}"
                   onclick="return confirm('삭제할까요?');">삭제</a>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty result.list}">
        <tr><td colspan="12">데이터가 없습니다.</td></tr>
    </c:if>
    </tbody>
</table>

<div class="mt-2">
    <a class="btn" href="${pageContext.request.contextPath}/grade/professor/add?courseId=${param.courseId}&subjectId=${param.subjectId}">+ 성적 등록</a>
</div>

<jsp:include page="/WEB-INF/views/common/page.jsp">
    <jsp:param name="baseUrl"    value="${pageContext.request.contextPath}/grade/professor/list"/>
    <jsp:param name="req"        value="${req}"/>
    <jsp:param name="result"     value="${result}"/>
    <jsp:param name="keepParams" value="${keepParams}"/>
</jsp:include>