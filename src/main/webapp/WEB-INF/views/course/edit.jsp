<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>강의 수정</title>
</head>
<body>
<h1>강의 수정</h1>
<form:form modelAttribute="courseUpdateRequestDTO" action="/courses/edit/${courseUpdateRequestDTO.id}" method="post">
    <div>
        <label for="capacity">수강 정원</label>
        <form:input path="capacity" type="number" min="1"/>
        <form:errors path="capacity"/>
    </div>
    <div>
        <label for="place">강의실</label>
        <form:input path="place"/>
        <form:errors path="place"/>
    </div>
    <div>
        <label for="dayOfWeek">요일</label>
        <form:input path="dayOfWeek"/>
        <form:errors path="dayOfWeek"/>
    </div>
    <div>
        <label for="time">시간</label>
        <form:input path="time"/>
        <form:errors path="time"/>
    </div>
    <button type="submit">수정 완료</button>
</form:form>
</body>
</html>
