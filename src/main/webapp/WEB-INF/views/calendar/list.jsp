<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>학사일정 목록</title>
    <link href='https://cdn.jsdelivr.net/npm/fullcalendar@6.1.11/main.min.css' rel='stylesheet' />
    <script src='https://cdn.jsdelivr.net/npm/fullcalendar@6.1.11/index.global.min.js'></script>
    <script src='https://cdn.jsdelivr.net/npm/@fullcalendar/core@6.1.11/locales/ko.js'></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

    <style>
        .container { max-width: 1000px; margin: 20px auto; }
        #calendar {
            width: 80%;
            max-width: 800px;
            margin: 0 auto 20px;
        }
        .calendar-list { margin-top: 20px; padding: 0; list-style: none; }
        .calendar-item { border: 1px solid #ccc; padding: 10px; margin-bottom: 10px; display: flex; justify-content: space-between; align-items: center; }
        .actions a, .actions button { margin-left: 10px; padding: 5px 10px; }
    </style>
</head>
<body>
<div class="container">
    <h1>학사일정</h1>
    <c:if test="${not empty message}">
        <div class="alert alert-success">
            <c:out value="${message}" />
        </div>
    </c:if>

    <div id='calendar'></div>

    <sec:authorize access="hasAnyRole('STAFF', 'ADMIN')">
        <div class="add-link" style="text-align: right; margin-bottom: 15px;">
            <a href="/calendar/add" class="btn btn-primary">새 일정 추가</a>
        </div>
    </sec:authorize>

    <hr/>

    <h2>일정 목록</h2>
    <ul class="calendar-list">
        <c:forEach var="calendar" items="${calendars}">
            <li class="calendar-item">
                <div>
                    <h2><c:out value="${calendar.content}"/></h2>
                    <p><c:out value="${calendar.startDate}"/> ~ <c:out value="${calendar.endDate}"/></p>
                </div>
                <sec:authorize access="hasAnyRole('STAFF', 'ADMIN')">
                    <div class="actions">
                        <a href="/calendar/edit/${calendar.id}">수정</a>
                        <button type="button" onclick="deleteSchedule(${calendar.id})">삭제</button>
                    </div>
                </sec:authorize>
            </li>
        </c:forEach>
    </ul>
</div>

<script>

    const isStaffOrAdmin =
        <sec:authorize access="hasAnyRole('STAFF', 'ADMIN')">true</sec:authorize>
    <sec:authorize access="!hasAnyRole('STAFF', 'ADMIN')">false</sec:authorize>;

    function deleteSchedule(id) {
        if (!confirm('정말로 이 일정을 삭제하시겠습니까?')) return;

        if (!isStaffOrAdmin) {
            alert('일정 삭제 권한이 없습니다.');
            return;
        }

        $.ajax({
            url: `/api/calendar/` + id,
            type: 'DELETE',
            xhrFields: { withCredentials: true },
            success: function(response) {
                alert(response);
                location.reload();
            },
            error: function(xhr) {
                let errorMessage = '삭제 중 오류가 발생했습니다.';
                if (xhr.status === 401) {
                    errorMessage = '인증이 만료되었습니다. 다시 로그인해주세요.';
                    window.location.href = '/login';
                } else if (xhr.status === 403) {
                    errorMessage = '권한이 없습니다. 교직원/관리자만 삭제할 수 있습니다.';
                }
                alert(errorMessage);
            }
        });
    }

    //  FullCalendar 초기화 및 데이터 로드
    document.addEventListener('DOMContentLoaded', function() {
        var calendarEl = document.getElementById('calendar');
        var calendar = new FullCalendar.Calendar(calendarEl, {
            initialView: 'dayGridMonth',
            locale: 'ko',
            headerToolbar: {
                left: 'prev,next today',
                center: 'title',
                right: 'dayGridMonth,timeGridWeek,timeGridDay'
            },
            editable: false,

            events: {
                url: '/api/calendar/data',
                method: 'GET',
                failure: function() {
                    alert('일정 데이터를 불러오는 데 실패했습니다.');
                },
                success: function(events) {
                    return events.map(function(cal) {
                        let end = new Date(cal.endDate);
                        end.setDate(end.getDate() + 1);

                        return {
                            id: cal.id,
                            title: cal.content,
                            start: cal.startDate,
                            end: end.toISOString().split('T')[0],
                            color: cal.color || '#3366ff'
                        };
                    });
                }
            },

            eventClick: function(info) {
                if (isStaffOrAdmin) {
                    // 교직원/관리자 권한이 있는 경우에만 수정 페이지로 이동
                    location.href = `/calendar/edit/${info.event.id}`;
                } else {
                    // 권한이 없는 경우 안내 메시지 출력
                    alert('일정 수정 권한이 없습니다.');
                }
            }
        });
        calendar.render();
    });
</script>
</body>
</html>