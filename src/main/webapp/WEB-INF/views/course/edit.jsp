<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>강의 수정</title>
</head>
<body>
<h1>강의 수정</h1>
<form action="/courses/update" method="post">
    <input type="hidden" name="id" value="${course.courseId}">

    학기 ID: <input type="number" name="semesterId" value="${course.semester.id}"><br/>
    정원: <input type="number" name="capacity" value="${course.capacity}"><br/>
    요일: <input type="text" name="dayOfWeek" value="${course.dayOfWeek}"><br/>
    장소: <input type="text" name="place" value="${course.place}"><br/>
    시간: <input type="text" name="time" value="${course.time}"><br/>
    <button type="submit">수정 완료</button>
</form>
</body>
</html>