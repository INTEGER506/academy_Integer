<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>학기 관리</title>

</head>
<body>
<h1>학기 관리</h1>

<!-- 학기 등록 폼 -->
<h2>학기 등록</h2>
<c:if test="${not empty message}">
    <p style="color: green; font-weight: bold;">${message}</p>
</c:if>

<c:if test="${not empty error}">
    <p style="color: red; font-weight: bold;">${error}</p>
</c:if>
<form action="/admin/semester/register" method="post">
    <input type="text" name="name" placeholder="학기명 (예: 2025-1학기)" required>
    <div>
        <label for="startDate">수강신청 시작일:</label>
        <input type="datetime-local" id="startDate" name="enrollmentStartDate" required> </div>

    <div>
        <label for="endDate">수강신청 종료일:</label>
        <input type="datetime-local" id="endDate" name="enrollmentEndDate" required> </div>
    <button type="submit">등록</button>
</form>

<!-- 학기 목록 -->
<h2>등록된 학기</h2>
<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>학기명</th>
        <th>수강신청 시작일</th>
        <th>수강신청 종료일</th>
        <th>관리</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="semester" items="${semesters}">
        <tr>
            <!-- 학기 수정 form -->
            <td>${semester.id}</td>
            <td colspan="3">
                <form action="/admin/semester/update" method="post" style="display:flex; gap:5px; align-items:center;">
                    <input type="hidden" name="id" value="${semester.id}">
                    <input type="text" name="name" value="${semester.name}" required>
                    <input type="datetime-local" name="enrollmentStartDate" value="${semester.enrollmentStartDate}" required>
                    <input type="datetime-local" name="enrollmentEndDate" value="${semester.enrollmentEndDate}" required>
                    <button type="submit">수정</button>
                </form>
            </td>

            <!-- 학기 삭제 form -->
            <td>
                <form action="/admin/semester/delete" method="post" style="display:inline;">
                    <input type="hidden" name="id" value="${semester.id}">
                    <button type="submit" onclick="return confirm('정말 삭제하시겠습니까?')">삭제</button>
                </form>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>
