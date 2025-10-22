<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>학사일정 관리 | 학사정보관리시스템</title>

    <!-- 폰트 & 부트스트랩 -->
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;700&family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>"/>

    <!-- FullCalendar -->
    <link href='https://cdn.jsdelivr.net/npm/fullcalendar@6.1.11/main.min.css' rel='stylesheet'/>
    <script src='https://cdn.jsdelivr.net/npm/fullcalendar@6.1.11/index.global.min.js'></script>
    <script src='https://cdn.jsdelivr.net/npm/@fullcalendar/core@6.1.11/locales/ko.js'></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

    <link rel="icon" href="data:,">
</head>

<body class="bg-page">

<%@ include file="/WEB-INF/views/components/header.jsp" %>

<main class="py-4">
    <div class="container-1200 d-flex gap-24">
        <%@ include file="/WEB-INF/views/components/sidebar.jsp" %>

        <section class="flex-1 d-flex flex-column gap-24">
            <!-- 타이틀 영역 -->
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h4 class="fw-bold mb-0">학사일정 관리</h4>

                <sec:authorize access="hasAnyRole('STAFF','ADMIN')">
                    <a href="<c:url value='/calendar/add'/>" class="btn btn-navy rounded-pill px-3">
                        <i class="bi bi-plus-circle"></i> 새 일정 추가
                    </a>
                </sec:authorize>
            </div>

            <!-- FullCalendar 카드 -->
            <div class="card-white p-4">
                <div id="calendar"></div>
            </div>

            <!-- 일정 목록 카드 -->
            <div class="card-white p-4">
                <h5 class="fw-bold mb-3">일정 목록</h5>

                <c:if test="${empty calendars}">
                    <div class="text-gray-500 small text-center py-4">등록된 학사일정이 없습니다.</div>
                </c:if>

                <c:forEach var="calendar" items="${calendars}">
                    <div class="d-flex justify-content-between align-items-center border-bottom py-2">
                        <div>
                            <div class="fw-semibold">${calendar.content}</div>
                            <div class="text-gray-600 small">
                                ${calendar.startDate} ~ ${calendar.endDate}
                            </div>
                        </div>

                        <sec:authorize access="hasAnyRole('STAFF','ADMIN')">
                            <div class="d-flex gap-2">
                                <a href="/calendar/edit/${calendar.id}" class="btn btn-outline-secondary btn-sm rounded-pill">
                                    <i class="bi bi-pencil-square"></i> 수정
                                </a>
                                <button type="button"
                                        class="btn btn-outline-danger btn-sm rounded-pill"
                                        onclick="deleteSchedule(${calendar.id})">
                                    <i class="bi bi-trash"></i> 삭제
                                </button>
                            </div>
                        </sec:authorize>
                    </div>
                </c:forEach>
            </div>
        </section>
    </div>
</main>

<%@ include file="/WEB-INF/views/components/footer.jsp" %>

<!-- 스크립트 -->
<script src="<c:url value='/vendor/jquery/jquery-3.7.1.min.js'/>"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
    const isStaffOrAdmin =
        <sec:authorize access="hasAnyRole('STAFF','ADMIN')">true</sec:authorize>
        <sec:authorize access="!hasAnyRole('STAFF','ADMIN')">false</sec:authorize>;

    // 일정 삭제
    function deleteSchedule(id) {
        if (!confirm('정말로 이 일정을 삭제하시겠습니까?')) return;
        if (!isStaffOrAdmin) {
            alert('삭제 권한이 없습니다.');
            return;
        }

        $.ajax({
            url: "/api/calendar/" + id,
            type: 'DELETE',
            success: function(res) {
                alert(res || "삭제되었습니다.");
                location.reload();
            },
            error: function(xhr) {
                let msg = "삭제 중 오류가 발생했습니다.";
                if (xhr.status === 403) msg = "권한이 없습니다. 교직원/관리자만 삭제 가능합니다.";
                alert(msg);
            }
        });
    }

    // FullCalendar 초기화
    document.addEventListener('DOMContentLoaded', function () {
        const calendarEl = document.getElementById('calendar');
        const calendar = new FullCalendar.Calendar(calendarEl, {
            initialView: 'dayGridMonth',
            locale: 'ko',
            headerToolbar: {
                left: 'prev,next today',
                center: 'title',
                right: 'dayGridMonth,timeGridWeek,timeGridDay'
            },
            height: 650,
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
                            color: cal.color || '#0d6efd'
                        };
                    });
                }
            },
            eventClick: function(info) {
                if (isStaffOrAdmin) {
                    location.href = `/calendar/edit/${info.event.id}`;
                } else {
                    alert('일정 수정 권한이 없습니다.');
                }
            }
        });
        calendar.render();
    });
</script>

</body>
</html>
