<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>학사정보관리시스템 - 알림 센터</title>

    <!-- 폰트/부트스트랩 -->
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>"/>
    <link rel="icon" href="data:,">
</head>
<body class="bg-page">

<%-- ✅ 공통 헤더 --%>
<%@ include file="/WEB-INF/views/components/header.jsp" %>

<main class="py-4">
    <div class="container-1200 d-flex gap-24">

        <%-- ✅ 사이드바 --%>
        <%@ include file="/WEB-INF/views/components/sidebar.jsp" %>

        <section class="flex-1 d-flex flex-column gap-24">
            <div class="card-white p-20 shadow-sm">
                <h5 class="fw-bold mb-3">알림 목록</h5>
                <button id="deleteAllResolved" class="btn btn-sm btn-outline-secondary">
                    <i class="bi bi-trash"></i> 읽음 알림 전체 삭제
                </button>

                <table class="table table-hover align-middle">
                    <thead class="table-light">
                    <tr>
                        <th>제목</th>
                        <th>상태</th>
                        <th>생성일</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="noti" items="${notifications}">
                        <tr class="${noti.isResolved eq 1 ? 'resolved' : 'unread'}"
                            data-noti-id="${noti.notiId}">
                            <td>${noti.title}</td>
                            <td>${noti.isResolved eq 1 ? '읽음' : '안읽음'}</td>
                            <td>${noti.createdAtStr}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </section>
    </div>
</main>

<%-- ✅ 공통 footer --%>
<%@ include file="/WEB-INF/views/components/footer.jsp" %>

<!-- 스크립트 -->
<script src="<c:url value='/vendor/jquery/jquery-3.7.1.min.js'/>"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        const token = localStorage.getItem('ACCESS_TOKEN') || '';

        // const targetId = document.querySelector('.card-white').dataset.userId;


        const unreadRows = document.querySelectorAll('tr.unread');
        unreadRows.forEach(row => {
            row.addEventListener('click', function () {
                const notiId = this.dataset.notiId;

                // notiId 값을 확인하는 디버깅 코드
                console.log('notiId:', notiId);
                if (!notiId) {
                    console.error('notiId is missing or invalid.');
                    return; // notiId가 없으면 요청을 보내지 않음
                }

                fetch('/api/notifications/' + notiId + '/read', {
                    method: 'POST',
                    headers: {
                        "Authorization": `Bearer ${token}`
                    }
                })
                    .then(response => {
                        if (response.ok) {
                            this.classList.remove('unread');
                            this.classList.add('resolved');
                            this.querySelector('td:nth-child(2)').textContent = '읽음';
                        }else{
                            console.error('Server responded with status: ', response.status);
                        }
                    })
                    .catch(err => console.error('Fetch error:', err));
            });
        });

        //일괄 삭제 로직 추가
        const deleteAllResolvedBtn = document.getElementById('deleteAllResolved');

        deleteAllResolvedBtn.addEventListener('click', function (){
            if (!confirm('읽음 상태의 알림을 모두 삭제하시겠습니까?')) return;

            fetch('/api/notifications/resolved', { // 🌟 경로에서 /resolved/targetId 를 제거
                method: 'DELETE',
                headers: {
                    "Authorization": `Bearer ${token}`
                }
            })
                .then(response => {
                    if (response.ok || response.status === 204) {
                        document.querySelectorAll('tr.resolved').forEach(row => row.remove());
                        alert('읽음 상태의 알림이 모두 삭제되었습니다.');
                    } else {
                        // 🌟 서버가 인증 실패(401/403)를 반환하면 로그인 관련 오류로 처리
                        console.error('삭제 실패, 상태 코드: ', response.status);
                        alert('삭제 실패. 권한 및 서버 상태를 확인하세요.');
                    }
                })
                .catch(err => console.error('Fetch error:', err));
        });
    });
</script>
</body>
</html>