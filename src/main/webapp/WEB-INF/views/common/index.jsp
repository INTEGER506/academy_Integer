<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>학사정보관리시스템 - 대시보드</title>

    <!-- 폰트/부트스트랩 -->
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>"/>
    <link rel="icon" href="data:,">
</head>
<body class="bg-page">

<%-- 헤더 --%>
<%@ include file="/WEB-INF/views/components/header.jsp" %>

<main class="py-4">
    <div class="container-1200 d-flex gap-24">

        <%-- 사이드바 --%>
        <%@ include file="/WEB-INF/views/components/sidebar.jsp" %>

        <section class="flex-1 d-flex flex-column gap-24">

            <%-- 대시보드 카드 --%>
            <div class="row g-3">
                <div class="col-12 col-md-6 col-lg-3">
                    <div class="card-white p-20 card-filter-item">
                        <div class="text-gray-600 xsmall mb-1">재학생 수</div>
                        <div class="fs-24 fw-700">1,234명</div>
                        <div class="xsmall text-gray-500 mt-2">업데이트: 2025-10-01 12:00</div>
                    </div>
                </div>

                <div class="col-12 col-md-6 col-lg-3">
                    <div class="card-white p-20 card-filter-item">
                        <div class="text-gray-600 xsmall mb-1">교수 수</div>
                        <div class="fs-24 fw-700">87명</div>
                        <div class="xsmall text-gray-500 mt-2">업데이트: 2025-10-01 12:00</div>
                    </div>
                </div>

                <div class="col-12 col-md-6 col-lg-3">
                    <div class="card-white p-20 card-filter-item">
                        <div class="text-gray-600 xsmall mb-1">개설 강좌</div>
                        <div class="fs-24 fw-700">156개</div>
                        <div class="xsmall text-gray-500 mt-2">업데이트: 2025-10-01 12:00</div>
                    </div>
                </div>

                <div class="col-12 col-md-6 col-lg-3">
                    <div class="card-white p-20 card-filter-item">
                        <div class="text-gray-600 xsmall mb-1">공지사항</div>
                        <div class="fs-24 fw-700">12건</div>
                        <div class="xsmall text-gray-500 mt-2">업데이트: 2025-10-01 12:00</div>
                    </div>
                </div>
            </div>

            <%-- 인원 분포 그래프 & 알림센터 --%>
            <div class="row g-3">
                <!-- 인원 분포 그래프 -->
                <div class="col-12 col-lg-6">
                    <div class="card-white p-20 h-100">
                        <h5 class="mb-3">학교 인원 분포</h5>
                        <canvas id="populationChart" height="200"></canvas>
                    </div>
                </div>

                <!-- 알림센터 -->
                <div class="col-12 col-lg-6">
                    <div class="card-white p-20 h-100">
                        <h5 class="mb-3 d-flex justify-content-between align-items-center">
                            알림센터
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
    </div>


    </section>
    </div>
</main>

<%-- 푸터 --%>
<%@ include file="/WEB-INF/views/components/footer.jsp" %>

<!-- 스크립트 -->
<script src="<c:url value='/vendor/jquery/jquery-3.7.1.min.js'/>"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
<script>
    // ✅ 인원 분포 차트 (하드코딩)
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
            plugins: {
                legend: { position: 'bottom' }
            }
        }
    });
</script>
<script>    //로그아웃 로직
document.getElementById('logoutForm').addEventListener('submit', function (event){
    event.preventDefault();

    //JWT 토큰 삭제
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('jwtToken');    //로그 모니터링에 사용된 토큰

    //접속기록 남기기 위한 POST 요청
    fetch('/api/auth/logout', {
        method: 'POST'
    })
        .then(response => {
            //서버 응답 성공 시 (접속 기록 성공) -> 로그아웃 후 로그인 페이지로 이동
            window.location.href = '/auth/login';
        })
        .catch(error => {
            console.error('로그아웃 API 호출 오류: ', error);
            alert("로그아웃 처리중 오류 발생했으나, 로컬 인증 정보 삭제 완료");
            window.location.href = "/auth/login";
        })
});
</script>
<script>
    document.addEventListener("DOMContentLoaded", function() {

        const token = localStorage.getItem("accessToken");
        const refreshBtn = document.getElementById("refreshNotiBtn");
        const listEl = document.getElementById("notificationList");

        if (!refreshBtn || !listEl) {
            console.error("알림센터 요소를 찾을 수 없습니다. HTML 구조 확인 필요.");
            return;
        }

        // ✅ 알림 불러오기 함수
        async function loadNotifications() {
            listEl.innerHTML = `<li class="list-group-item text-center text-gray-500 small">로딩 중...</li>`;
            try {
                const res = await fetch("<c:url value='/api/notifications/me'/>", {
                    headers: { "Authorization": `Bearer ${token}` }
                });

                if (!res.ok) {
                    listEl.innerHTML = `<li class="list-group-item text-center text-danger small">
                    알림 불러오기 실패 (${res.status})
                </li>`;
                    return;
                }

                const data = await res.json();
                if (!data || data.length === 0) {
                    listEl.innerHTML = `<li class="list-group-item text-center text-gray-500 small">
                    새로운 알림이 없습니다.
                </li>`;
                    return;
                }

                // ✅ 알림 렌더링
                listEl.innerHTML = "";
                data.slice(0, 5).forEach(noti => {
                    const badgeClass = {
                        "urgent_notice": "bg-danger",
                        "deadline_reminder": "bg-warning",
                        "grade_announcement": "bg-success",
                    }[noti.notiType] || "bg-secondary";

                    const item = `
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        ${noti.title}
                        <span class="badge ${badgeClass} rounded-pill">
                            ${noti.notiType.replace("_", " ")}
                        </span>
                    </li>`;
                    listEl.insertAdjacentHTML("beforeend", item);
                });

            } catch (err) {
                console.error("알림센터 로드 실패:", err);
                listEl.innerHTML = `<li class="list-group-item text-center text-danger small">
                서버 오류로 알림을 불러올 수 없습니다.
            </li>`;
            }
        }

        // ✅ 새로고침 버튼 이벤트 연결
        refreshBtn.addEventListener("click", loadNotifications);

        // ✅ 페이지 로드 시 자동 실행
        loadNotifications();
    });
</script>
</body>
</html>
