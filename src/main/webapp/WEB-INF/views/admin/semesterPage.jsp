<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <title>학사정보관리시스템 - 학기 관리</title>

    <!-- 폰트 및 Bootstrap -->
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;700&family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>

    <!-- 공통 스타일 -->
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>"/>
    <link rel="icon" href="<c:url value='/favicon.ico'/>"/>
</head>
<body class="bg-page">
<jsp:include page="/WEB-INF/views/components/header.jsp"/>

<main class="container-1200 d-flex gap-24 py-4">
    <jsp:include page="/WEB-INF/views/components/sidebar.jsp"/>

    <section class="flex-1 card-white p-4 shadow-sm">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h4 class="fw-bold mb-0">학기 관리</h4>
        </div>

        <!-- 메시지 알림 -->
        <c:if test="${not empty message}">
            <div class="alert alert-success small">${message}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger small">${error}</div>
        </c:if>

        <!-- 학기 등록 -->
        <div class="card p-4 mb-4 shadow-sm">
            <h5 class="fw-semibold mb-3"><i class="bi bi-calendar-plus"></i> 학기 등록</h5>
            <form action="<c:url value='/semester/register'/>" method="post" class="row g-3">
                <div class="col-md-4">
                    <input type="text" name="name" class="form-control" placeholder="학기명 (예: 2025-1학기)" required>
                </div>
                <div class="col-md-3">
                    <label class="form-label small mb-1">수강신청 시작일</label>
                    <input type="datetime-local" name="enrollmentStartDate" class="form-control" required>
                </div>
                <div class="col-md-3">
                    <label class="form-label small mb-1">수강신청 종료일</label>
                    <input type="datetime-local" name="enrollmentEndDate" class="form-control" required>
                </div>
                <div class="col-md-2 d-flex align-items-end">
                    <button type="submit" class="btn btn-navy w-100 rounded-pill">
                        <i class="bi bi-plus-circle"></i> 등록
                    </button>
                </div>
            </form>
        </div>

        <!-- 학기 목록 -->
        <div class="card p-4 shadow-sm">
            <h5 class="fw-semibold mb-3"><i class="bi bi-list-check"></i> 등록된 학기</h5>

            <div class="table-responsive">
                <table class="table table-hover align-middle text-center">
                    <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>학기명</th>
                        <th>시작일</th>
                        <th>종료일</th>
                        <th>관리</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="semester" items="${semesters}">
                        <tr>
                            <td>${semester.id}</td>
                            <td>
                                <form action="<c:url value='/semester/update'/>" method="post" class="d-flex gap-2 justify-content-center align-items-center">
                                    <input type="hidden" name="id" value="${semester.id}">
                                    <input type="text" name="name" value="${semester.name}" class="form-control form-control-sm w-75" required>
                            </td>
                            <td>
                                <input type="datetime-local" name="enrollmentStartDate" value="${semester.enrollmentStartDate}" class="form-control form-control-sm" required>
                            </td>
                            <td>
                                <input type="datetime-local" name="enrollmentEndDate" value="${semester.enrollmentEndDate}" class="form-control form-control-sm" required>
                            </td>
                            <td class="text-nowrap">
                                <button type="submit" class="btn btn-sm btn-outline-navy rounded-pill px-3">
                                    <i class="bi bi-save"></i> 수정
                                </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </section>
</main>

<jsp:include page="/WEB-INF/views/components/footer.jsp"/>

<script src="<c:url value='/vendor/jquery/jquery-3.7.1.min.js'/>"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
