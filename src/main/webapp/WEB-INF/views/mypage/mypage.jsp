<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>마이페이지</title>

    <!-- 외부 CDN -->
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;700&family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>

    <!-- 정적 리소스 -->
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>"/>
    <link rel="icon" href="<c:url value='/favicon.ico'/>"/>
</head>
<body class="bg-page">
<jsp:include page="/WEB-INF/views/components/header.jsp"/>

<main class="container-1200 d-flex gap-24 py-4">
    <jsp:include page="/WEB-INF/views/components/sidebar.jsp"/>

    <section class="flex-1 card-white p-4 shadow-sm">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h4 class="fw-bold mb-0">내 정보 관리</h4>
            <button class="btn btn-navy rounded-pill px-3" id="saveBtn">
                <i class="bi bi-save"></i> 저장
            </button>
        </div>

        <!-- 사용자 기본 정보 -->
        <form id="myInfoForm" class="w-75">
            <!-- PK 필드 추가 -->
            <input type="hidden" id="userIdPk" name="id" />

            <div class="mb-3">
                <label class="form-label fw-semibold">아이디</label>
                <input type="text" id="username" class="form-control" readonly />
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">이름</label>
                <input type="text" id="name" class="form-control" readonly />
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">이메일</label>
                <input type="email" id="email" class="form-control" placeholder="이메일 입력" />
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">전화번호</label>
                <input type="text" id="phone" class="form-control" placeholder="전화번호 입력" />
            </div>

            <!-- 교직원 로그인시에는 학과 보이지 않도록 - JS 로직 추가 -->
            <div class="mb-3 staff-hidden-field">
                <label class="form-label fw-semibold">소속(부서 / 학과)</label>
                <input type="text" id="dept" class="form-control" readonly />
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">권한</label>
                <input type="text" id="role" class="form-control" readonly />
            </div>

            <!-- JS 에서 학생만 보이도록 로직 추가 -->
            <div class="mb-3 student-only-field">
                <label class="form-label fw-semibold">학적 상태</label>
                <input type="text" id="status" class="form-control" readonly />
            </div>

            <div class="mb-3 student-only-field">
                <label class="form-label fw-semibold">신청 학점</label>
                <input type="text" id="enrolledCredits" class="form-control" readonly />
            </div>

            <!-- ✅ 비밀번호 변경 섹션 추가 -->
            <hr class="my-4">
            <h5 class="fw-bold mb-3">비밀번호 변경</h5>

            <div class="mb-3">
                <label class="form-label fw-semibold">현재 비밀번호</label>
                <input type="password" id="currentPassword" class="form-control" placeholder="현재 비밀번호 입력" />
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">새 비밀번호</label>
                <input type="password" id="newPassword" class="form-control" placeholder="새 비밀번호 입력" />
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">새 비밀번호 확인</label>
                <input type="password" id="confirmPassword" class="form-control" placeholder="새 비밀번호 재입력" />
            </div>

            <button type="button" class="btn btn-outline-navy mt-2" id="changePwBtn">
                <i class="bi bi-key"></i> 비밀번호 변경
            </button>
        </form>
    </section>
</main>

<jsp:include page="/WEB-INF/views/components/footer.jsp"/>

<!-- 외부 JS -->
<script src="<c:url value='/vendor/jquery/jquery-3.7.1.min.js'/>"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<!-- JS 로직 -->
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script>
    const token = localStorage.getItem("accessToken");

    //권한 코드와 한국어 매핑
    const ROLE_MAPPING = {
        'ROLE_ADMIN': '관리자',
        'ROLE_PROFESSOR': '교수',
        'ROLE_ADVISOR': '지도교수',
        'ROLE_STUDENT': '학생',
        'ROLE_STAFF': '교직원'
    };

    // ✅ 내 정보 불러오기
    async function loadMyInfo() {
        const res = await fetch("${ctx}/api/mypage/me", {
            headers: { "Authorization": `Bearer ${token}` }
        });
        if (!res.ok) {
            alert("사용자 정보를 불러올 수 없습니다.");
            return;
        }

        const data = await res.json();
        const user = data.user;
        const role = data.role;     //MyPageInfoDTO

        $("#username").val(user.username);
        $("#name").val(user.name);
        $("#email").val(user.email);
        $("#phone").val(user.phone || '');

        //학과 - 관리자, 교직원일 경우 안보이도록
        if(user.role === 'ROLE_STAFF' || user.role === 'ROLE_ADMIN') {
            $(".staff-hidden-field").hide();
        } else {
            if($("#dept").length){
                $("#dept").val(role.deptName || '');
            }
        }

        //권한 필드 한국어 변환하여 표시
        const roleName = ROLE_MAPPING[user.role] || user.role;
        $("#role").val(roleName);

        //학생 전용 필드 (ROLE_STUDENT 일때만 존재)
        if(user.role === 'ROLE_STUDENT'){
            //학과 표시
            $(".staff-hidden-field").show();

            //학생 전용 필드 표시
            $(".student-only-field").show();

            //role 객체가 있을 경우
            if(role){
                if($("#dept").length){
                    $("#dept").val(role.deptName || '');
                }
                $("#status").val(role.status || '');
                $("#enrolledCredits").val(role.enrolledCredits || 0);
            } else {
                //role 객체가 null인 경우
                if($("#dept").length){
                    $("#dept").val('');
                }
                $("#status").val('');
                $("#enrolledCredits").val(0);
            }
        //교수, 지도교수
        } else if(user.role === 'ROLE_PROFESSOR' || user.role === 'ROLE_ADVISOR') {
            //학과 표시 - 학생과 동일한 필드 사용
            $(".staff-hidden-field").show();
            if($("#dept").length && role){
                $("#dept").val(role.deptName || '');
            }
        }else if(user.role === 'ROLE_STAFF'){
            $(".staff-hidden-field").hide();
        }else if(user.role === 'ROLE_ADMIN'){

        }
    }

    // ✅ 내 정보 수정
    async function updateMyInfo() {
        const user = {
            id: $("userIdPk").val(),    //PK값 추가
            username: $("#username").val(),
            email: $("#email").val(),
            phone: $("#phone").val()
        };

        //UpdateUserRequestDTO 구조에 맞게 body 생성
        const body = JSON.stringify({ user, roleEntity: null });

        const res = await fetch("${ctx}/api/mypage/update-me", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body
        });

        if (res.ok) {
            alert("정보가 성공적으로 수정되었습니다.");
            loadMyInfo();
        } else {
            alert("정보 수정 실패. 다시 시도해주세요.");
        }
    }

    // ✅ 비밀번호 변경
    async function changePassword() {
        const currentPassword = $("#currentPassword").val().trim();
        const newPassword = $("#newPassword").val().trim();
        const confirmPassword = $("#confirmPassword").val().trim();

        if (!currentPassword || !newPassword || !confirmPassword) {
            alert("모든 비밀번호 입력란을 작성해주세요.");
            return;
        }
        if (newPassword !== confirmPassword) {
            alert("새 비밀번호가 일치하지 않습니다.");
            return;
        }

        // ✅ 수정된 부분
        const res = await fetch("${ctx}/api/auth/change-password", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({
                currentPassword,
                newPassword
            })
        });

        if (res.ok) {
            alert("✅ 비밀번호가 성공적으로 변경되었습니다. 다시 로그인해주세요.");
            localStorage.removeItem("accessToken");
            localStorage.removeItem("refreshToken");

            location.href = "${ctx}/auth/login";
        } else {
            const msg = await res.text();
            alert("❌ 비밀번호 변경 실패: " + msg);
        }
    }


    $("#saveBtn").on("click", (e) => {
        e.preventDefault();
        updateMyInfo();
    });

    $("#changePwBtn").on("click", (e) => {
        e.preventDefault();
        changePassword();
    });

    $(document).ready(loadMyInfo);
</script>
</body>
</html>
