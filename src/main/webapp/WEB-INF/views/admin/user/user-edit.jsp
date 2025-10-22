<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <title>사용자 수정</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>"/>
</head>
<body class="bg-page">

<jsp:include page="/WEB-INF/views/components/header.jsp"/>

<main class="py-4">
    <div class="container-800">
        <div class="card-white p-4 shadow-sm">
            <h4 class="mb-3">사용자 수정</h4>
            <form id="editUserForm">
                <input type="hidden" id="userId" name="id"/>
                <div class="mb-3">
                    <label class="form-label">이름</label>
                    <input type="text" class="form-control" id="name" name="name"/>
                </div>
                <div class="mb-3">
                    <label class="form-label">이메일</label>
                    <input type="email" class="form-control" id="email" name="email"/>
                </div>
                <div class="mb-3">
                    <label class="form-label">상태</label>
                    <select id="status" name="status" class="form-select">
                        <option value="재학중">재학중</option>
                        <option value="휴학중">휴학중</option>
                        <option value="졸업">졸업</option>
                    </select>
                </div>
                <button type="submit" class="btn btn-primary">저장</button>
            </form>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/views/components/footer.jsp"/>

<script src="<c:url value='/js/vendor/jquery/jquery-3.7.1.min.js'/>"></script>
<script>
    const params = new URLSearchParams(window.location.search);
    const userId = params.get("id");

    $.get("<c:url value='/api/admin/user/all'/>", function(users){
        const u = users.find(x => x.id == userId);
        if(u){
            $("#userId").val(u.id);
            $("#name").val(u.name);
            $("#email").val(u.email);
            $("#status").val(u.status);
        }
    });

    $("#editUserForm").on("submit", function(e){
        e.preventDefault();
        const data = {
            user: { id: $("#userId").val(), name: $("#name").val(), email: $("#email").val() },
            roleEntity: { roleName: "STUDENT" } // TODO: 필요하면 선택 UI
        };
        $.ajax({
            url: "<c:url value='/api/admin/update-user/'/>" + userId,
            type: "PUT",
            data: JSON.stringify(data),
            contentType: "application/json",
            success: () => alert("수정 완료"),
            error: () => alert("수정 실패")
        });
    });
</script>
</body>
</html>
