<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>학사정보관리시스템 - 공지사항</title>

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
                    <div class="fw-700 fs-5">📢 공지사항</div>

                    <sec:authorize access="hasRole('ROLE_ADMIN')">
                        <a href="<c:url value='/notices/add'/>" class="btn btn-sm btn-primary">
                            <i class="bi bi-plus-circle"></i> 새 공지 등록
                        </a>
                    </sec:authorize>
                </div>

                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th>번호</th>
                        <th>제목</th>
                        <th>시작일</th>
                        <th>종료일</th>
                        <th>등록일</th>
                        <th>조회수</th>
                        <th>긴급</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="notice" items="${noticeList}">
                        <tr>
                            <td>${notice.id}</td>
                            <td>
                                <a href="<c:url value='/notices/detail/${notice.id}'/>">${notice.title}</a>
                            </td>
                            <!-- LocalDate → String 게터 사용 -->
                            <td>${notice.startDateStr}</td>
                            <td>${notice.endDateStr}</td>
                            <td>${notice.createdAtStr}</td>
                            <td>${notice.viewCount}</td>
                            <td>
                                <c:if test="${notice.isUrgent == 1}">
                                    <span class="badge bg-danger">긴급</span>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
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
