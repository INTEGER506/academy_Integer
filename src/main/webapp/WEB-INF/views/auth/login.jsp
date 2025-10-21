<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>로그인</title>

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
            <div class="brand-title">학사정보관리시스템</div>
            <div class="brand-sub">University Management System</div>
        </div>

        <!-- 로그인 폼 -->
        <form id="loginForm">
            <input class="form-control mb-2" id="username" name="username" placeholder="아이디" required/>
            <input class="form-control mb-3" id="password" type="password" name="password" placeholder="비밀번호" required/>
            <button type="submit" class="btn btn-navy w-100 rounded-pill mb-2">로그인</button>
        </form>

        <div class="text-center">
            <a href="<c:url value='/auth/find-password'/>" class="xsmall text-navy">비밀번호 찾기</a>
        </div>
    </div>
</div>

<!-- 외부 JS -->
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script>
    document.getElementById("loginForm").addEventListener("submit", async (e) => {
        e.preventDefault();
        const username = document.getElementById("username").value.trim();
        const password = document.getElementById("password").value;

        const res = await fetch("${ctx}/api/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password })
        });

        if (!res.ok) { alert("로그인 실패"); return; }

        const data = await res.json();
        localStorage.setItem("accessToken", data.accessToken);
        localStorage.setItem("refreshToken", data.refreshToken);

        if (data.isTempPassword) {
            location.href = "${ctx}/auth/change-password";
            return;
        }
        location.href = "${ctx}/index";
    });
</script>




</body>
</html>
