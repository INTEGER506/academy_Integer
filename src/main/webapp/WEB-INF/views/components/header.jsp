<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<header class="app-header bg-header border-bottom">
    <div class="container-1200 d-flex align-items-center justify-content-between gap-3">

        <!-- 좌: 로고 -->
        <a href="<c:url value='/index'/>"
           class="d-flex align-items-center gap-2 text-decoration-none">
            <img src="<c:url value='/favicon-32x32.png'/>" alt="Logo" width="32" height="32"/>
            <div>
                <div class="brand-title">학사정보관리시스템</div>
                <div class="brand-sub">University Management System</div>
            </div>
        </a>


        <!-- 우: 알림 / 로그아웃 / 프로필 -->
        <div class="d-flex align-items-center gap-2">
            <!-- 알림 -->
            <button class="icon-pill" title="알림">
                <i class="bi bi-bell"></i>
                <!-- 미확인 알림이 있다면 표시 -->
                <c:if test="${not empty unreadNotifications && unreadNotifications > 0}">
                    <span class="badge-dot">${unreadNotifications}</span>
                </c:if>
            </button>

            <!-- 로그아웃 -->
            <a href="#" id="logoutA" onclick="logout()"
               class="btn btn-white rounded-pill h-40 px-3 d-flex align-items-center gap-1">
                <i class="bi bi-box-arrow-right"></i><span>로그아웃</span>
            </a>

            <!-- 사용자 프로필: 권한별 분기 -->
            <sec:authorize access="hasRole('ROLE_ADMIN')">
                <a href="<c:url value='/admin/mypage'/>"
                   class="btn btn-white rounded-pill h-40 px-2 d-flex align-items-center gap-2 text-decoration-none">
                    <div class="avatar">M</div>
                    <div class="text-start lh-1">
                        <div class="text-navy fw-600 small"><sec:authentication property="principal.name"/></div>
                        <div class="text-gray-500 xsmall">관리자</div>
                    </div>
                </a>
            </sec:authorize>

            <sec:authorize access="hasRole('ROLE_PROFESSOR')">
                <a href="<c:url value='/professor/mypage'/>"
                   class="btn btn-white rounded-pill h-40 px-2 d-flex align-items-center gap-2 text-decoration-none">
                    <div class="avatar">P</div>
                    <div class="text-start lh-1">
                        <div class="text-navy fw-600 small"><sec:authentication property="principal.name"/></div>
                        <div class="text-gray-500 xsmall">교수</div>
                    </div>
                </a>
            </sec:authorize>

            <sec:authorize access="hasRole('ROLE_ADVISOR')">
                <a href="<c:url value='/advisor/mypage'/>"
                   class="btn btn-white rounded-pill h-40 px-2 d-flex align-items-center gap-2 text-decoration-none">
                    <div class="avatar">A</div>
                    <div class="text-start lh-1">
                        <div class="text-navy fw-600 small"><sec:authentication property="principal.name"/></div>
                        <div class="text-gray-500 xsmall">지도교수</div>
                    </div>
                </a>
            </sec:authorize>

            <sec:authorize access="hasRole('ROLE_STUDENT')">
                <a href="<c:url value='/student/mypage'/>"
                   class="btn btn-white rounded-pill h-40 px-2 d-flex align-items-center gap-2 text-decoration-none">
                    <div class="avatar">S</div>
                    <div class="text-start lh-1">
                        <div class="text-navy fw-600 small"><sec:authentication property="principal.name"/></div>
                        <div class="text-gray-500 xsmall">학생</div>
                    </div>
                </a>
            </sec:authorize>

        </div>
    </div>
</header>

<script>
    function logout() {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");

        fetch('<c:url value="/api/auth/logout"/>', { method: 'POST' })
            .then(() => window.location.href = '<c:url value="/auth/login"/>')
            .catch(error => {
                console.error('로그아웃 API 오류:', error);
                alert("로그아웃 처리중 오류 발생했으나, 로컬 인증 정보는 삭제됨");
                window.location.href = '<c:url value="/auth/login"/>';
            });
    }
</script>
