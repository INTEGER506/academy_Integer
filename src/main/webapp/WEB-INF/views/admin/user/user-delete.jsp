<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <title>사용자 관리 - 삭제</title>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;700&family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>"/>
</head>
<body class="bg-page">
<jsp:include page="/WEB-INF/views/components/header.jsp"/>
<main class="container-1200 d-flex gap-24 py-4">
    <jsp:include page="/WEB-INF/views/components/sidebar.jsp"/>
    <section class="flex-1 card-white p-4 shadow-sm">
        <h4 class="fw-bold mb-3">사용자 삭제</h4>
        <p>정말로 삭제하시겠습니까?</p>
        <button id="deleteBtn" class="btn btn-danger"><i class="bi bi-trash"></i> 삭제</button>
    </section>
</main>
<jsp:include page="/WEB-INF/views/components/footer.jsp"/>
<script src="<c:url value='/vendor/jquery/jquery-3.7.1.min.js'/>"></script>
<script>
    const id = "${userId}";   // 문자열로 바꿔야 안전함
    $("#deleteBtn").click(function(){
        if(confirm("삭제하시겠습니까?")){
            $.ajax({
                url: "${pageContext.request.contextPath}/api/admin/delete-user/" + id,
                type: "DELETE",
                success: () => {
                    alert("삭제 완료");
                    // 목록으로 이동
                    window.location.href = "${pageContext.request.contextPath}/auth/user-list";
                },
                error: () => {
                    alert("삭제 실패");
                }
            });
        }
    });
</script>
</body>
</html>
