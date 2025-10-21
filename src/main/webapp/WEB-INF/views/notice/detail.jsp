<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>학사정보관리시스템 - 공지 상세</title>

    <!-- ✅ 공통 CSS -->
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>"/>
    <!-- ✅ 파비콘 -->
    <link rel="icon" type="image/x-icon" href="<c:url value='/favicon.ico'/>"/>
</head>
<body class="bg-page page-notice">

<!-- ✅ header -->
<jsp:include page="/WEB-INF/views/components/header.jsp"/>

<main class="py-4">
    <div class="container-1200 d-flex gap-24">

        <!-- ✅ sidebar -->
        <jsp:include page="/WEB-INF/views/components/sidebar.jsp"/>

        <!-- ✅ 본문 -->
        <section class="flex-1">
            <div class="card-white p-20">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <div class="fw-700 fs-5">📌 공지 상세</div>
                </div>

                <div class="mb-4">
                    <h4 class="fw-600 mb-2">${notice.title}</h4>
                    <p class="text-gray-500 small">
                        등록일: ${notice.createdAtStr} &nbsp;|&nbsp; 조회수: ${notice.viewCount}
                    </p>
                    <p class="small">
                        시작일: ${notice.startDateStr} /
                        종료일: ${notice.endDateStr} /
                        마감일: ${notice.deadlineStr}
                    </p>
                    <p>
                        긴급 여부:
                        <c:if test="${notice.isUrgent == 1}">
                            <span class="badge bg-danger">긴급</span>
                        </c:if>
                        <c:if test="${notice.isUrgent == 0}">
                            <span class="badge bg-secondary">일반</span>
                        </c:if>
                    </p>
                    <hr>
                    <div class="mt-3">${notice.content}</div>
                </div>

                <!-- 버튼 -->
                <div class="mt-3 d-flex gap-2">
                    <a href="<c:url value='/notices'/>" class="btn btn-secondary">
                        <i class="bi bi-list"></i> 목록
                    </a>

                    <sec:authorize access="hasRole('ROLE_ADMIN')">
                        <a href="<c:url value='/notices/edit/${notice.id}'/>" class="btn btn-warning">
                            <i class="bi bi-pencil-square"></i> 수정
                        </a>
                        <form action="<c:url value='/notices/delete'/>" method="post" style="display:inline;">
                            <input type="hidden" name="id" value="${notice.id}"/>
                            <button type="submit" class="btn btn-danger">
                                <i class="bi bi-trash"></i> 삭제
                            </button>
                        </form>
                    </sec:authorize>
                </div>
            </div>
        </section>
    </div>
</main>

<!-- ✅ footer -->
<jsp:include page="/WEB-INF/views/components/footer.jsp"/>

<!-- ✅ JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
