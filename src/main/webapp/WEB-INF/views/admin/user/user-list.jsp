<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <title>사용자 관리 - 목록</title>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;700&family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
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
        <div class="d-flex justify-content-between mb-3">
            <h4 class="fw-bold">사용자 목록</h4>
            <!-- 계정 추가 버튼: user-create.jsp 열기 -->
            <a href="<c:url value='/auth/user-create'/>" class="btn btn-navy">
                <i class="bi bi-person-plus"></i> 계정 추가
            </a>
        </div>


        <div class="mb-3">
            <label class="form-label">권한 필터</label>
            <select id="roleFilter" class="form-select w-auto">
                <option value="">전체</option>
                <option value="ADMIN">관리자</option>
                <option value="PROFESSOR">교수</option>
                <option value="ADVISOR">지도 교수</option>
                <option value="STUDENT">학생</option>
            </select>
        </div>

        <table class="table table-hover align-middle">
            <thead>
            <tr><th>ID</th><th>이름</th><th>이메일</th><th>권한</th><th>상태</th></tr>
            </thead>
            <tbody id="userTableBody"></tbody>
        </table>
    </section>
</main>

<jsp:include page="/WEB-INF/views/components/footer.jsp"/>
<%--jquery 스크립트 경로 수정--%>
<script src="<c:url value='/vendor/jquery/jquery-3.7.1.min.js'/>"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function loadUsers(role){
        let url = "<c:url value='/api/admin/user/all'/>";

        if(role)
            url = "<c:url value='/api/admin/user/role'/>?role=" + role;

        $.get(url, function(users){
            // 🔹 ID 오름차순 정렬 추가
            users.sort((a, b) => a.id - b.id);

            let rows = "";
            users.forEach(u => {
                rows += `
<tr>
    <td>\${u.id}</td>
    <td>
        <a href="${pageContext.request.contextPath}/auth/user-detail?id=\${u.id}"
           class="text-navy fw-600">
           \${u.name}
        </a>
    </td>
    <td>\${u.email}</td>
    <td>\${u.role}</td>
    <td>\${u.passwordTemp ? '임시비번' : '정상'}</td>
</tr>`;
            });
            $("#userTableBody").html(rows);
        });
    }

    $("#roleFilter").on("change", ()=>loadUsers($("#roleFilter").val()));
    $(function(){ loadUsers(); });
</script>

</body>
</html>
