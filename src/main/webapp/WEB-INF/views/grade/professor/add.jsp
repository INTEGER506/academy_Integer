<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>성적 등록</h2>

<form method="post" action="${pageContext.request.contextPath}/grade/professor/add">
    <input type="hidden" name="enrollmentId" value="${param.enrollmentId}"/>
    <input type="hidden" name="courseId"     value="${param.courseId}"/>
    <input type="hidden" name="subjectId"    value="${param.subjectId}"/>

    <div>
        <label>중간</label>
        <input type="number" name="midExam" min="0" max="100" required/>
    </div>
    <div>
        <label>기말</label>
        <input type="number" name="finalExam" min="0" max="100" required/>
    </div>
    <div>
        <label>과제</label>
        <input type="number" name="assignment" min="0" max="100" required/>
    </div>
    <div>
        <label>출석</label>
        <input type="number" name="attendance" min="0" max="100" required/>
    </div>

    <div class="mt-2">
        <button type="submit">저장</button>
        <a href="${pageContext.request.contextPath}/grade/professor/list?courseId=${param.courseId}&subjectId=${param.subjectId}">목록</a>
    </div>
</form>