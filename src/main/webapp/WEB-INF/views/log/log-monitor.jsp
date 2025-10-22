<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>학사정보관리시스템 - 로그 모니터링</title>

    <!-- 폰트/부트스트랩 -->
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>"/>
    <link rel="icon" href="data:,">

    <style>
        /* ✅ 로그 모니터링 전용 확장 스타일 */
        .container-1200 {
            max-width: 1600px !important; /* 기존 1200px → 1600px 확장 */
            margin: 0 auto;
        }

        #logArea {
            background: #000;
            color: #00FF00;
            padding: 16px;
            height: 70vh;
            overflow-y: auto;
            border-radius: 8px;
            font-family: Consolas, monospace;
            white-space: pre-wrap;
            word-break: break-word;
            font-size: 13px;
            line-height: 1.4;
        }

        @media (max-width: 768px) {
            #logArea {
                height: 60vh;
                font-size: 12px;
            }
        }
    </style>
</head>
<body class="bg-page">

<%@ include file="/WEB-INF/views/components/header.jsp" %>

<main class="py-4">
    <div class="container-1200 d-flex gap-24">

        <%@ include file="/WEB-INF/views/components/sidebar.jsp" %>

        <section class="flex-1">
            <div class="card-white p-20">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h4 class="mb-0">시스템 관리 - 로그 모니터링</h4>
                    <button class="btn btn-sm btn-primary" onclick="loadLogs()">새로고침</button>
                </div>

                <pre id="logArea"></pre>
            </div>
        </section>
    </div>
</main>

<%@ include file="/WEB-INF/views/components/footer.jsp" %>

<script>
    async function loadLogs() {
        const logArea = document.getElementById('logArea');
        const jwtToken = localStorage.getItem('accessToken');

        if (!jwtToken) {
            logArea.textContent = "⚠️ JWT 토큰이 없습니다. 로그인 상태를 확인하세요.";
            return;
        }

        try {
            const res = await fetch('/api/admin/logs/monitor', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${jwtToken.trim()}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!res.ok) {
                throw new Error(`로그 API 호출 실패: ${res.status} ${res.statusText}`);
            }

            const data = await res.json();
            const logLines = data.logs || [];
            let displayContent = '';

            if (logLines.length > 0) {
                displayContent = logLines.join('\n');
            } else if (data.message) {
                displayContent = data.message;
            } else {
                displayContent = "로그 데이터가 없습니다.";
            }

            logArea.textContent = displayContent;
            logArea.scrollTop = logArea.scrollHeight;
        } catch (e) {
            console.error("로그 로딩 실패: ", e);
            logArea.textContent = "❌ 로그 로딩에 실패했습니다. 관리자 권한을 확인해주세요.";
        }
    }

    document.addEventListener('DOMContentLoaded', function () {
        loadLogs();
        setInterval(loadLogs, 5000); // 필요시 자동 새로고침
    });
</script>

</body>
</html>
