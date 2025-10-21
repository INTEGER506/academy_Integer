<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <title>사용자 관리 - 계정 생성</title>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;700&family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>"/>
    <link rel="icon" href="<c:url value='/favicon.ico'/>"/>
</head>

<body class="bg-page">
<!-- ✅ 공통 Header -->
<jsp:include page="/WEB-INF/views/components/header.jsp"/>

<main class="container-1200 d-flex gap-24 py-4">
    <!-- ✅ 공통 Sidebar -->
    <jsp:include page="/WEB-INF/views/components/sidebar.jsp"/>

    <!-- ✅ 카드 형태의 본문 -->
    <section class="flex-1 card-white p-4 shadow-sm">
        <h4 class="fw-bold mb-3">계정 생성</h4>

        <div class="card p-4 shadow-sm border-0">
            <form id="createAccountForm" action="/api/admin/create-account" method="post">
                <label for="role">권한 :</label><br>
                <select id="role" name="role" required>
                    <option value="ROLE_STUDENT">학생</option>
                    <option value="ROLE_PROFESSOR">교수</option>
                    <option value="ROLE_STAFF">교직원</option>
                </select><br><br>

                <!--학생 계정일 때만 보이는 학과ID 입력 필드-->
                <div id="deptIdGroup" style="display: none;">
                    <label for="deptId">학과 ID: </label><br>
                    <input type="number" id="deptId" name="deptId" min="1"><br><br>
                </div>

                <label for="email">이메일: </label><br>
                <input type="email" id="email" name="email" required><br><br>

                <label for="name">이름: </label><br>
                <input type="text" id="name" name="name" required><br><br>

                <button type="submit" class="btn btn-navy w-100">계정 생성</button>
            </form>

            <!--서버 응답 메시지 표시 (성공/실패 텍스트)-->
            <div id="message" class="mt-3"></div>
        </div>
    </section>
</main>

<!-- ✅ 공통 Footer -->
<jsp:include page="/WEB-INF/views/components/footer.jsp"/>

<!-- ✅ JS -->
<script src="<c:url value='/vendor/jquery/jquery-3.7.1.min.js'/>"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<!-- ✅ 원본 스크립트 그대로 유지 -->
<script>
    document.addEventListener('DOMContentLoaded', function (){
        const roleSelect = document.getElementById('role');
        const deptIdGroup = document.getElementById('deptIdGroup');
        const deptIdInput = document.getElementById('deptId');
        const form = document.getElementById('createAccountForm');
        const messageDiv = document.getElementById('message');

        //역할에 따라 deptId 필드 숨기기 - 직원일 때만 학과 필드 안보이기
        function toggleDeptField(){
            if(roleSelect.value === 'ROLE_STUDENT' || roleSelect.value === 'ROLE_PROFESSOR' || roleSelect.value === 'ROLE_ADVISOR'){
                deptIdGroup.style.display = 'block';
                deptIdInput.required = true;
            }else{
                deptIdGroup.style.display = 'none';
                deptIdInput.required = false;
                deptIdInput.value = '';
            }
        }

        //초기 상태 적용
        toggleDeptField();

        //역할 변경 시 실행
        roleSelect.addEventListener('change', toggleDeptField);

        //폼 제출 (AJAX)
        form.addEventListener('submit', function (event){
            event.preventDefault();

            const formData = new FormData(form);    //폼 데이터 수집
            const searchParams = new URLSearchParams();

            for(const pair of formData){
                //deptId는 학생이 아닐 경우 빈 값으로 전송
                //pair[0] - input의 name 속성, pair[1] - 사용자가 입력한 값
                if(pair[0] === 'deptId' && pair[1].trim() === '') continue; //continue = 현재 반복을 건너뛰고 다음 pair로 넘어감
                searchParams.append(pair[0], pair[1]);
            }

            //POST 요청
            fetch(form.action, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: searchParams // URLSearchParams는 Content-Type: application/x-www-form-urlencoded로 전송됨
            })
                .then(response => {
                    messageDiv.style.color = response.ok ? 'green' : 'red';
                    return response.text().then(text => ({ ok: response.ok, text }));
                })
                .then(data => {
                    messageDiv.innerHTML = (data.ok ? '성공: ' : '실패: ') + data.text;
                    if(data.ok){
                        form.reset();
                        toggleDeptField();  //초기 상태로 복원
                    }
                })
                .catch(error => {
                    console.error('Network Error:', error);
                    messageDiv.innerHTML = '오류 발생: 네트워크 연결 상태를 확인해주세요.';
                });
        });
    });
</script>
</body>
</html>
