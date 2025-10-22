<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>학사정보관리시스템 - 대시보드</title>

    <!-- ✅ 폰트 / 부트스트랩 / 공통 style -->
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>"/>
    <link rel="icon" href="data:,">
</head>

<body class="bg-page">
<%@ include file="/WEB-INF/views/components/header.jsp" %>

<main class="py-4">
    <div class="container-1200 d-flex gap-24">
        <%@ include file="/WEB-INF/views/components/sidebar.jsp" %>

        <section class="flex-1 d-flex flex-column gap-24">

            <!-- ✅ 주요 통계 카드 -->
            <div class="row g-3">
                <div class="col-12 col-md-6 col-lg-3">
                    <div class="card-white p-20">
                        <div class="text-gray-600 xsmall mb-1">재학생 수</div>
                        <div class="fs-24 fw-700">1,234명</div>
                        <div class="xsmall text-gray-500 mt-2">업데이트: 2025-10-01 12:00</div>
                    </div>
                </div>

                <div class="col-12 col-md-6 col-lg-3">
                    <div class="card-white p-20">
                        <div class="text-gray-600 xsmall mb-1">교수 수</div>
                        <div class="fs-24 fw-700">87명</div>
                        <div class="xsmall text-gray-500 mt-2">업데이트: 2025-10-01 12:00</div>
                    </div>
                </div>

                <div class="col-12 col-md-6 col-lg-3">
                    <div class="card-white p-20">
                        <div class="text-gray-600 xsmall mb-1">개설 강좌</div>
                        <div class="fs-24 fw-700">156개</div>
                        <div class="xsmall text-gray-500 mt-2">업데이트: 2025-10-01 12:00</div>
                    </div>
                </div>

                <div class="col-12 col-md-6 col-lg-3">
                    <div class="card-white p-20">
                        <div class="text-gray-600 xsmall mb-1">공지사항</div>
                        <div class="fs-24 fw-700">12건</div>
                        <div class="xsmall text-gray-500 mt-2">업데이트: 2025-10-01 12:00</div>
                    </div>
                </div>
            </div>

            <!-- ✅ 인원 분포 & 알림센터 -->
            <div class="row g-3">
                <!-- 인원 분포 차트 -->
                <div class="col-12 col-lg-6">
                    <div class="card-white p-20 h-100">
                        <h5 class="mb-3 fw-600"><i class="bi bi-people"></i> 학교 인원 분포</h5>
                        <canvas id="populationChart" height="200"></canvas>
                    </div>
                </div>

                <!-- 알림센터 -->
                <div class="col-12 col-lg-6">
                    <div class="card-white p-20 h-100">
                        <h5 class="mb-3 d-flex justify-content-between align-items-center fw-600">
                            <span><i class="bi bi-bell"></i> 알림센터</span>
                            <button class="btn btn-sm btn-outline-navy rounded-pill" id="refreshNotiBtn">
                                <i class="bi bi-arrow-clockwise"></i> 새로고침
                            </button>
                        </h5>
                        <ul class="list-group" id="notificationList">
                            <li class="list-group-item text-center text-gray-500 small">알림을 불러오는 중...</li>
                        </ul>
                    </div>
                </div>
            </div>

        </section>
    </div>
</main>

<%@ include file="/WEB-INF/views/components/footer.jsp" %>

<!-- ✅ JS 라이브러리 -->
<script src="<c:url value='/vendor/jquery/jquery-3.7.1.min.js'/>"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>

<!-- ✅ 인원 분포 차트 (하드코딩) -->
<script>
    const ctx = document.getElementById('populationChart');
    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['재학생', '교수', '직원'],
            datasets: [{
                data: [1234, 87, 45],
                backgroundColor: ['#0d6efd', '#198754', '#6c757d']
            }]
        },
        options: {
            responsive: true,
            plugins: { legend: { position: 'bottom' } }
        }
    });
</script>

<!-- ✅ 로그아웃 로직 -->
<script>
    document.getElementById('logoutForm').addEventListener('submit', async function (e) {
        e.preventDefault();

        // 토큰 삭제
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('jwtToken');

        try {
            await fetch('/api/auth/logout', { method: 'POST' });
        } catch (err) {
            console.warn("로그아웃 API 호출 실패:", err);
        } finally {
            window.location.href = "/auth/login";
        }
    });
</script>

<script>
    document.addEventListener("DOMContentLoaded", function() {
        const refreshBtn = document.getElementById("refreshNotiBtn");
        const listEl = document.getElementById("notificationList");

        if (!refreshBtn || !listEl) {
            console.error("⚠️ 알림센터 요소를 찾을 수 없습니다. HTML 구조 확인 필요.");
            return;
        }

        // ✅ 하드코딩 알림 데이터
        const hardcodedNotifications = [
            { id: 1, title: "📢 중간고사 일정이 공지되었습니다.", notiType: "urgent_notice", createdAt: "2025-10-20 09:00" },
            { id: 2, title: "🧾 과제 제출 마감 2일 전입니다.", notiType: "deadline_reminder", createdAt: "2025-10-21 13:00" },
            { id: 3, title: "📊 성적 공지: 데이터베이스 프로그래밍", notiType: "grade_announcement", createdAt: "2025-10-21 18:30" },
            { id: 4, title: "📅 강의평가 기간이 시작되었습니다.", notiType: "urgent_notice", createdAt: "2025-10-22 08:00" },
            { id: 5, title: "📘 학사 안내: 수강신청 변경 마감 D-1", notiType: "deadline_reminder", createdAt: "2025-10-22 10:30" }
        ];

        // ✅ 알림 렌더링 함수
        function renderNotifications(data) {
            listEl.innerHTML = "";
            data.forEach(noti => {
                const badgeClass = {
                    "urgent_notice": "bg-danger",
                    "deadline_reminder": "bg-warning",
                    "grade_announcement": "bg-success"
                }[noti.notiType] || "bg-secondary";

                const item = `
                <li class="list-group-item d-flex justify-content-between align-items-center">
                    <div class="text-start">
                        <div class="fw-500">${noti.title}</div>
                        <div class="small text-gray-600">${noti.createdAt}</div>
                    </div>
                    <span class="badge ${badgeClass} rounded-pill text-white">
                        ${noti.notiType.replace("_", " ").toUpperCase()}
                    </span>
                </li>`;
                listEl.insertAdjacentHTML("beforeend", item);
            });
        }

        // ✅ 새로고침 버튼 클릭 시 재랜더링
        refreshBtn.addEventListener("click", () => {
            console.log("🔄 알림 새로고침 실행");
            renderNotifications(hardcodedNotifications);
        });

        // ✅ 페이지 로드 시 즉시 표시
        renderNotifications(hardcodedNotifications);
    });
</script>


</body>
</html>
