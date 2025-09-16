<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>강의 개설</title>
</head>
<body>
<h1>강의 개설</h1>

<!-- 폼 유효성 검사 오류 메시지 표시 -->
<form:form action="/courses/add" method="post" modelAttribute="courseCreateRequestDTO">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

    <label for="semesterId">학기 ID:</label>
    <form:input type="number" path="semesterId" required="true"/><br/>
    <form:errors path="semesterId" style="color: red;"/><br/>

    <label for="professorId">교수 ID:</label>
    <form:input type="number" path="professorId" required="true"/><br/>
    <form:errors path="professorId" style="color: red;"/><br/>

    <label for="subjectId">과목 ID:</label>
    <form:input type="number" path="subjectId" required="true"/><br/>
    <form:errors path="subjectId" style="color: red;"/><br/>

    <label for="capacity">정원:</label>
    <form:input type="number" path="capacity" min="4" max="30" required="true"/><br/>
    <form:errors path="capacity" style="color: red;"/><br/>

    <label for="place">강의실:</label>
    <form:input type="text" path="place" required="true"/><br/>
    <form:errors path="place" style="color: red;"/><br/>

    <label for="dayOfWeek">요일:</label>
    <form:input type="text" path="dayOfWeek" required="true"/><br/>
    <form:errors path="dayOfWeek" style="color: red;"/><br/>

    <label for="time">시간:</label>
    <form:input type="text" path="time" required="true"/><br/>
    <form:errors path="time" style="color: red;"/><br/>

    <button type="submit">개설</button>
</form:form>
</body>
</html>
