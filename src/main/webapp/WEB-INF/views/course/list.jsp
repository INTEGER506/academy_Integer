<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>강의 목록</title>
</head>
<body>
<div>
    <h1>강의 목록</h1>
    <c:if test="${not empty message}">
        <div>
                ${message}
        </div>
    </c:if>
    <c:if test="${not empty error}">
        <div>
                ${error}
        </div>
    </c:if>

    <div>
        <a href="/courses/add">강의 개설</a>
    </div>

    <c:choose>
        <c:when test="${not empty courses}">
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>과목명</th>
                    <th>교수명</th>
                    <th>수강인원</th>
                    <th>정원</th>
                    <th>요일</th>
                    <th>시간</th>
                    <th>장소</th>
                    <th>학점</th>
                    <th>상태</th>
                    <th>액션</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="course" items="${courses}">
                    <tr>
                        <td>${course.id}</td>
                        <td><a href="/courses/${course.id}">${course.subjectName}</a></td>
                        <td>${course.professorName}</td>
                        <td>${course.numOfStudent}</td>
                        <td>${course.capacity}</td>
                        <td>${course.dayOfWeek}</td>
                        <td>${course.time}</td>
                        <td>${course.place}</td>
                        <td>${course.credit}</td>
                        <td>
                                    <span>
                                            ${course.status}
                                    </span>
                        </td>
                        <td>
                            <a href="/courses/edit/${course.id}">수정</a>
                            <form action="/courses/delete/${course.id}" method="post" onsubmit="return confirm('정말로 이 강의를 삭제하시겠습니까?');">
                                <button type="submit">삭제</button>
                            </form>
                            <c:if test="${course.status eq '개설'}">
                                <form action="/courses/close/${course.id}" method="post" onsubmit="return confirm('정말로 이 강의를 폐강하시겠습니까?');">
                                    <button type="submit">폐강</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <p>등록된 강의가 없습니다.</p>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>
