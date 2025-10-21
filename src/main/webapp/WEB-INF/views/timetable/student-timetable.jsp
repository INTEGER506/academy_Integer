<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <meta charset="UTF-8">
    <title>내 시간표</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .timetable td, .timetable th {
            border: 1px solid #dee2e6;
            height: 50px;
            vertical-align: middle;
            text-align: center;
        }
        .timetable th {
            background-color: #f8f9fa;
        }
        .course-cell {
            background-color: #cfe2ff;
            border-radius: 6px;
            padding: 2px 4px;
            font-size: 0.85rem;
        }
        .timetable {
            table-layout: fixed;
            border-collapse: collapse;
        }
    </style>
</head>
<body class="p-4">
<div class="container">
    <h1 class="mb-4">학생 시간표</h1>

    <!-- 요일 및 교시 정의 -->
    <c:set var="days" value="${['월','화','수','목','금']}"/>
    <c:set var="periods" value="${[
        '1교시 (09:00~09:50)',
        '2교시 (10:00~10:50)',
        '3교시 (11:00~11:50)',
        '4교시 (12:00~12:50)',
        '5교시 (13:00~13:50)',
        '6교시 (14:00~14:50)',
        '7교시 (15:00~15:50)',
        '8교시 (16:00~16:50)',
        '9교시 (17:00~17:50)',
        '10교시 (18:00~18:50)'
    ]}"/>
    <c:set var="timeSlots" value="${[
        '09:00~09:50',
        '10:00~10:50',
        '11:00~11:50',
        '12:00~12:50',
        '13:00~13:50',
        '14:00~14:50',
        '15:00~15:50',
        '16:00~16:50',
        '17:00~17:50',
        '18:00~18:50'
    ]}"/>

    <div class="table-responsive">
        <table class="table timetable">
            <thead>
            <tr>
                <th style="width: 60px;">교시</th>
                <c:forEach var="day" items="${days}">
                    <th style="width: 80px;">${day}</th>
                </c:forEach>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="period" items="${periods}" varStatus="status">
                <tr>
                    <th>${period}</th>
                    <c:forEach var="day" items="${days}">
                        <td>
                            <!-- 현재 교시의 시작 시간 추출 -->
                            <c:set var="slot" value="${timeSlots[status.index]}"/>
                            <c:set var="slotStart" value="${fn:substringBefore(slot, '~')}"/>
                            <c:set var="slotStartNum" value="${fn:replace(slotStart, ':', '')}"/>

                            <!-- 학생의 모든 수강 과목 탐색 -->
                            <c:forEach var="course" items="${courses}">
                                <c:if test="${course.dayOfWeek eq day}">
                                    <c:set var="start" value="${empty course.startTime ? fn:substringBefore(course.time, '~') : course.startTime}" />
                                    <c:set var="startNum" value="${fn:replace(start, ':', '')}"/>

                                    <!-- 현재 교시와 강의 시작 시간이 일치하는 경우 표시 -->
                                    <c:if test="${slotStartNum eq startNum}">
                                        <div class="course-cell">
                                                ${course.subjectName}<br/>
                                                ${course.professorName}<br/>
                                                ${course.place}
                                        </div>
                                    </c:if>
                                </c:if>
                            </c:forEach>
                        </td>
                    </c:forEach>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
