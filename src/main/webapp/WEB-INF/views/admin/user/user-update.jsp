<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <title>사용자 관리 - 수정</title>
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
        <h4 class="fw-bold mb-3">사용자 수정</h4>
        <form id="updateForm">
            <input type="hidden" id="id" name="id" value="${userId}">
            <div class="mb-3">
                <label class="form-label">이름</label>
                <input type="text" id="name" class="form-control"/>
            </div>
            <div class="mb-3">
                <label class="form-label">이메일</label>
                <input type="email" id="email" class="form-control"/>
            </div>
            <div class="mb-3">
                <label class="form-label">전화번호</label>
                <input type="text" id="phone" class="form-control"/>
            </div>
            <button type="submit" class="btn btn-navy"><i class="bi bi-check"></i> 저장</button>
        </form>
    </section>
</main>
<jsp:include page="/WEB-INF/views/components/footer.jsp"/>
<script src="<c:url value='/vendor/jquery/jquery-3.7.1.min.js'/>"></script>
<script>
    const id = ${userId};
    $(function(){
        $.get("<c:url value='/api/admin/user/'/>" + id, function(u){
            $("#name").val(u.name);
            $("#email").val(u.email);
            $("#phone").val(u.phone);
        });
    });
    $("#updateForm").submit(function(e){
        e.preventDefault();
        const data = {
            user: {
                id: id,
                name: $("#name").val(),
                email: $("#email").val(),
                phone: $("#phone").val()
            },
            roleEntity: null
        };
        $.ajax({
            url: "<c:url value='/api/admin/update-user/'/>" + id,
            type: "PUT",
            data: JSON.stringify(data),
            contentType: "application/json",
            success: ()=>alert("수정 완료")
        });
    });
</script>
</body>
</html>
