<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>지도교수 권한 관리 - 관리자</title>

    <!-- 폰트 / 부트스트랩 / 공용 CSS -->
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>

<body class="bg-page">
<jsp:include page="/WEB-INF/views/components/header.jsp"/>

<main class="container-1200 d-flex gap-24 py-4">
    <jsp:include page="/WEB-INF/views/components/sidebar.jsp"/>

    <section class="flex-1 card-white p-4 shadow-sm">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h4 class="fw-bold">지도교수 권한 관리</h4>

            <div class="d-flex align-items-center gap-2">
                <label for="roleFilter" class="fw-semibold mb-0">권한 필터:</label>
                <select id="roleFilter" class="form-select w-auto">
                    <option value="">전체</option>
                    <option value="ROLE_PROFESSOR">교수</option>
                    <option value="ROLE_ADVISOR">지도교수</option>
                </select>
                <button class="btn btn-navy" id="reloadBtn">
                    <i class="bi bi-arrow-repeat"></i> 새로고침
                </button>
            </div>
        </div>

        <!--교수/지도교수 리스트 -->
        <table class="table table-hover align-middle text-center">
            <thead class="table-light">
            <tr>
                <th>이름</th>
                <th>아이디</th>
                <th>이메일</th>
                <th>전화번호</th>
                <th>학과</th>
                <th>현재 권한</th>
                <th>작업</th>
            </tr>
            </thead>
            <tbody id="professorTableBody">
            <tr><td colspan="7" class="text-muted">데이터를 불러오는 중...</td></tr>
            </tbody>
        </table>

        <!--학과 선택 모달 -->
        <div class="modal fade" id="deptModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content rounded-3">
                    <div class="modal-header">
                        <h5 class="modal-title fw-bold">학과 선택</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form id="deptSelectForm">
                            <input type="hidden" id="selectedProfessorId">
                            <div class="mb-3">
                                <label class="form-label">대상 학과 선택</label>
                                <select id="deptSelect" class="form-select" required>
                                    <option value="">-- 학과 선택 --</option>
                                    <c:forEach var="dept" items="${deptList}">
                                        <option value="${dept.id}">${dept.deptName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="d-flex justify-content-end gap-2">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                                <button type="submit" class="btn btn-primary">확인</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <!--메시지 -->
        <div id="message" class="alert mt-3 text-center small" style="display:none;"></div>
    </section>
</main>

<jsp:include page="/WEB-INF/views/components/footer.jsp"/>

<!-- 스크립트 -->
<script src="<c:url value='/vendor/jquery/jquery-3.7.1.min.js'/>"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    let professors = [];
    const messageDiv = document.getElementById("message");

    function showMessage(text, type="info") {
        messageDiv.style.display = "block";
        messageDiv.className = "alert alert-" + type;
        messageDiv.innerText = text;
    }

    //null 안전 출력
    function safe(v) {
        return (v === null || v === undefined || v === "" || v === false) ? "-" : v;
    }

    //리스트 불러오기
    function loadProfessors(roleFilter) {
        $("#professorTableBody").html(`<tr><td colspan="7" class="text-gray-500">불러오는 중...</td></tr>`);

        let url = "<c:url value='/api/admin/user/all'/>";
        if (roleFilter && roleFilter.trim() !== "") {
            url = `<c:url value='/api/admin/user/role'/>?role=\${roleFilter}`; // JSP와 JS 충돌 방지
        }

        $.get(url, function(users) {
            professors = users.filter(u => u.role === "ROLE_PROFESSOR" || u.role === "ROLE_ADVISOR");
            renderTable();
        }).fail(() => {
            $("#professorTableBody").html(`<tr><td colspan="7" class="text-danger">데이터를 불러오는 중 오류 발생</td></tr>`);
        });
    }

    //테이블 렌더링
    function renderTable() {
        if (!professors || professors.length === 0) {
            $("#professorTableBody").html(`<tr><td colspan="7" class="text-muted">등록된 교수가 없습니다.</td></tr>`);
            return;
        }

        professors.sort((a,b) => a.id - b.id);
        let rows = "";
        professors.forEach(u => {
            const isAdvisor = u.role === "ROLE_ADVISOR";
            const btn = isAdvisor
                ? `<button class="btn btn-outline-danger btn-sm rounded-pill" onclick="revertAdvisor(\${u.id})">권한 해제</button>`
                : `<button class="btn btn-primary btn-sm rounded-pill" onclick="openDeptModal(\${u.id})">권한 지정</button>`;

            rows += `
            <tr>
                <td>\${safe(u.name)}</td>
                <td>\${safe(u.username)}</td>
                <td>\${safe(u.email)}</td>
                <td>\${safe(u.phone)}</td>
                <td>\${safe(u.deptName)}</td>
                <td>\${isAdvisor ? '지도교수' : '교수'}</td>
                <td>\${btn}</td>
            </tr>`;
        });
        $("#professorTableBody").html(rows);
    }

    //학과 선택 모달 열기
    function openDeptModal(profId) {
        $("#selectedProfessorId").val(profId);
        const modal = new bootstrap.Modal(document.getElementById("deptModal"));
        modal.show();
    }

    //지도교수 지정
    $("#deptSelectForm").on("submit", function(e) {
        e.preventDefault();
        const professorUserId = $("#selectedProfessorId").val();
        const deptId = $("#deptSelect").val();

        if (!deptId) return alert("학과를 선택하세요.");

        $.post("<c:url value='/api/admin/assign-advisor'/>", { professorUserId, deptId })
            .done(msg => {
                showMessage(msg, "success");
                loadProfessors($("#roleFilter").val());
                bootstrap.Modal.getInstance(document.getElementById("deptModal")).hide();
            })
            .fail(err => showMessage("지정 실패: " + err.responseText, "danger"));
    });

    //지도교수 권한 회수
    function revertAdvisor(professorUserId) {
        if (!confirm("이 교수의 지도교수 권한을 해제하시겠습니까?")) return;

        $.post("<c:url value='/api/admin/revert-advisor-role'/>", { professorUserId })
            .done(msg => {
                showMessage(msg, "warning");
                loadProfessors($("#roleFilter").val());
            })
            .fail(err => showMessage("회수 실패: " + err.responseText, "danger"));
    }

    //필터 및 새로고침
    $("#roleFilter").on("change", () => loadProfessors($("#roleFilter").val()));
    $("#reloadBtn").on("click", () => loadProfessors($("#roleFilter").val()));

    //페이지 로드시 전체 불러오기
    $(function() { loadProfessors(); });
</script>
</body>
</html>