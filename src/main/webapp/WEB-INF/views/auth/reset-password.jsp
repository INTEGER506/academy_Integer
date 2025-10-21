<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>비밀번호 초기화 - 관리자</title>

    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>

    <link rel="stylesheet" href="<c:url value='/css/style.css'/>"/>
    <link rel="icon" href="<c:url value='/favicon.ico'/>"/>
</head>

<body class="bg-page">
<jsp:include page="/WEB-INF/views/components/header.jsp"/>

<main class="container-1200 d-flex gap-24 py-4">
    <jsp:include page="/WEB-INF/views/components/sidebar.jsp"/>

    <section class="flex-1 card-white p-4 shadow-sm">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h4 class="fw-bold mb-0">비밀번호 초기화</h4>
            <a href="<c:url value='/auth/user-list'/>" class="btn btn-outline-secondary btn-sm">
                <i class="bi bi-arrow-left"></i> 목록으로
            </a>
        </div>

        <div class="alert alert-warning small mb-4">
            ⚠️ 이 기능은 <strong>관리자 전용</strong>이며, 선택한 사용자의 비밀번호를 **서버에서 생성된 임시 비밀번호**로 초기화합니다.
        </div>

        <form id="resetForm" class="w-50">
            <div class="mb-3">
                <label for="targetUsername" class="form-label fw-semibold">사용자 아이디</label>
                <input type="text" id="targetUsername" name="targetUsername" class="form-control" placeholder="아이디 입력" required>
            </div>

            <div class="d-flex justify-content-end gap-2 mt-4">
                <button type="button" class="btn btn-outline-secondary rounded-pill" onclick="history.back()">취소</button>
                <button type="submit" class="btn btn-danger rounded-pill px-4">비밀번호 초기화</button>
            </div>

            <div id="message" class="mt-3"></div>
        </form>
    </section>
</main>

<jsp:include page="/WEB-INF/views/components/footer.jsp"/>

<script src="<c:url value='/vendor/jquery/jquery-3.7.1.min.js'/>"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script>
    const resetForm = document.getElementById("resetForm");
    const messageDiv = document.getElementById("message");

    // 메시지를 표시하는 함수 (순수 문자열 연결 '+' 사용)
    function showMessage(text, type, tempPassword = null) {
        messageDiv.innerHTML = '';
        messageDiv.style.padding = '8px';
        messageDiv.style.marginBottom = '10px';
        messageDiv.style.borderRadius = '4px';
        messageDiv.style.border = '1px solid';

        var html = '';

        if (type === 'success') {
            // 성공 스타일 (Bootstrap alert-success 역할)
            messageDiv.style.color = '#155724';
            messageDiv.style.backgroundColor = '#d4edda';
            messageDiv.style.borderColor = '#c3e6cb';

            // HTML 문자열 연결
            html = html + '<div style="font-weight: bold;">' + text + '</div>';

            if (tempPassword) {
                html = html + '<hr style="margin: 4px 0;">';
                html = html + '<div>임시 비밀번호: <strong style="color: #007bff;">' + tempPassword + '</strong></div>';
            }

        } else {
            // 실패 스타일 (Bootstrap alert-danger 역할)
            messageDiv.style.color = '#721c24';
            messageDiv.style.backgroundColor = '#f8d7da';
            messageDiv.style.borderColor = '#f5c6cb';

            // HTML 문자열 연결
            html = html + '비밀번호 초기화 실패: ' + text;
        }

        messageDiv.innerHTML = html;
    }


    // ✅ 비밀번호 초기화 요청 처리
    resetForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const username = document.getElementById("targetUsername").value.trim();
        const token = localStorage.getItem("accessToken");

        // 메시지 영역 초기화
        messageDiv.style.border = 'none';
        messageDiv.innerHTML = '';

        if (!username) {
            showMessage("사용자 ID를 입력해주세요.", "error");
            return;
        }

        try {
            const res = await fetch("${ctx}/api/admin/reset-password", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify({ username: username })
            });

            // 인증/권한 오류 (401/403) 처리
            if (res.status === 401 || res.status === 403) {
                showMessage("인증 오류: 관리자 권한이 없거나 세션이 만료되었습니다. 재로그인이 필요합니다.", "error");
                return;
            }

            // 응답 데이터 파싱
            let resetResponse;
            try {
                resetResponse = await res.json();
            } catch (e) {
                // JSON 파싱 오류 시 (HTML 응답 등)
                const responseText = await res.text();

                if (responseText.trim().startsWith('<!DOCTYPE html>')) {
                    console.error("서버에서 HTML 에러 페이지가 반환되었습니다. 백엔드 로그 확인 필요.");
                    showMessage("요청 처리 중 서버 내부 오류가 발생했습니다. (백엔드 로그 확인 필요)", "error");
                    return;
                }

                resetResponse = { message: responseText };
            }

            // 1차 호출 실패 (비밀번호 초기화 자체 실패)
            if (!res.ok) {
                const msg = resetResponse.message || resetResponse.toString() || "알 수 없는 오류";
                showMessage(msg, "error");
                return;
            }

            // 1차 호출 성공: 임시 비밀번호 화면 표시
            const tempPassword = resetResponse.tempPassword;
            const successMessage = resetResponse.message || "비밀번호가 성공적으로 초기화되었습니다.";
            const userId = resetResponse.userId;

            showMessage(successMessage, "success", tempPassword);
            resetForm.reset();  // 폼 내용 초기화

        } catch (error) {
            // 서버 통신 오류
            console.error("서버 통신 중 오류 발생:", error);
            showMessage("서버 통신 중 오류가 발생했습니다.", "error");
        }
    });
</script>
</body>
</html>