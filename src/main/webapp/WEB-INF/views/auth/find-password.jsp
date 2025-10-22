<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>비밀번호 찾기</title>

    <!-- 외부 CDN -->
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>

    <!-- 정적 리소스 -->
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>"/>
</head>
<body class="bg-page">
<div class="login-wrapper">
    <div class="card-white p-4 login-card">
        <div class="text-center mb-3">
            <div class="login-logo-wrap">
                <img class="login-logo" src="<c:url value='/img/logo.png'/>" alt="Winston College 로고"/>
            </div>
            <div class="brand-title">비밀번호 찾기</div>
            <div class="brand-sub">학번과 이메일/전화번호를 입력하세요</div>
        </div>

        <!-- 비밀번호 찾기 폼 -->
        <form id="findPasswordForm">
            <input type="text" class="form-control mb-2" id="studentId" name="studentId" placeholder="학번" required />
            <input type="text" class="form-control mb-3" id="contact" name="contact" placeholder="이메일 또는 전화번호" required />
            <button type="submit" class="btn btn-navy w-100 rounded-pill mb-2">비밀번호 변경하기</button>
        </form>

        <div class="text-center mt-2">
            <a href="<c:url value='/auth/login'/>" class="xsmall text-navy">← 로그인 화면으로</a>
        </div>
    </div>
</div>

<!-- 외부 JS -->
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script>
    document.getElementById("findPasswordForm").addEventListener("submit", async (e) => {
        e.preventDefault();

        const studentId = document.getElementById("studentId").value.trim();
        const contact = document.getElementById("contact").value.trim();

        if (!studentId || !contact) {
            alert("모든 항목을 입력해주세요.");
            return;
        }

        try {
            const res = await fetch("${ctx}/api/auth/find-password", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ studentId, contact })
            });

            if (res.status === 200) {
                alert("본인 확인이 완료되었습니다. 비밀번호를 변경해주세요.");
                location.href = "${ctx}/auth/change-password";
            } else {
                alert("입력한 정보와 일치하는 계정이 없습니다.");
            }
        } catch (err) {
            alert("서버 오류가 발생했습니다.");
        }
    });
</script>
</body>
</html>
