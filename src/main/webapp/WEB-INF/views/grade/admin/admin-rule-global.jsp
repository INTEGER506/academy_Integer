<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>글로벌 성적 규정 - 학사정보관리시스템</title>

    <!-- 폰트 / 부트스트랩 / 공통 style -->
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>"/>
</head>

<body class="bg-page">
<%@ include file="/WEB-INF/views/components/header.jsp" %>

<main class="py-4">
    <div class="container-1200 d-flex gap-24">
        <%@ include file="/WEB-INF/views/components/sidebar.jsp" %>

        <!-- 메인 컨텐츠 -->
        <section class="flex-1 d-flex flex-column gap-24">
            <div class="d-flex align-items-center justify-content-between mb-4">
                <div>
                    <a href="/" class="btn btn-outline-secondary">
                        <i class="bi bi-house-door"></i> 메인페이지
                    </a>
                    <a href="/grade/admin/subject-rules" class="btn btn-outline-primary ms-2">
                        ← 과목별 규정 목록
                    </a>
                </div>
                <h2 class="fw-bold text-navy">글로벌 성적 규정</h2>
            </div>

            <p class="text-gray-600 small">
                학교 전체에 적용되는 기본 성적 분배 비율입니다. (상위 누적 비율)
            </p>

            <div class="card-white p-20" style="max-width: 600px; margin: 0 auto;">
                <table class="table table-hover align-middle text-center">
                    <thead class="table-navy text-white">
                    <tr>
                        <th>학점</th>
                        <th>상위 누적 비율 (%)</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="row" items="${result.data}" varStatus="st">
                        <tr class="${st.index % 2 == 0 ? '' : 'table-light'}">
                            <td class="fw-bold fs-6">${row.alphabet}</td>
                            <td>
                                <span class="display-mode" data-alphabet="${row.alphabet}">
                                    ${row.boundary}
                                </span>
                                <input type="number"
                                       class="edit-mode form-control d-inline-block text-center"
                                       name="boundary_${row.alphabet}"
                                       value="${row.boundary}"
                                       min="0" max="100" step="0.1"
                                       style="display:none; width:100px;">
                                <input type="hidden" name="alphabet_${row.alphabet}" value="${row.alphabet}">
                            </td>
                        </tr>
                    </c:forEach>

                    <!-- 기본값 (데이터 없을 경우) -->
                    <c:if test="${empty result.data}">
                        <c:set var="defaultGrades">A+:5,A:20,B+:20,B:20,C+:10,C:10,D+:3,D:2</c:set>
                        <c:forTokens var="g" items="${defaultGrades}" delims=",">
                            <c:set var="grade" value="${fn:split(g, ':')[0]}"/>
                            <c:set var="percent" value="${fn:split(g, ':')[1]}"/>
                            <tr>
                                <td class="fw-bold fs-6">${grade}</td>
                                <td>
                                    <span class="display-mode" data-alphabet="${grade}">${percent}</span>
                                    <input type="number"
                                           class="edit-mode form-control d-inline-block text-center"
                                           name="boundary_${grade}"
                                           value="${percent}"
                                           min="0" max="100" step="0.1"
                                           style="display:none; width:100px;">
                                    <input type="hidden" name="alphabet_${grade}" value="${grade}">
                                </td>
                            </tr>
                        </c:forTokens>
                    </c:if>
                    </tbody>
                </table>
            </div>

            <!-- 버튼 영역 -->
            <div class="text-center mt-4">
                <div id="editButtons" style="display:none;">
                    <button onclick="saveChanges()" class="btn btn-success px-4 me-2">
                        저장
                    </button>
                    <button onclick="cancelEdit()" class="btn btn-secondary px-4">
                        취소
                    </button>
                </div>

                <div id="viewButtons">
                    <button onclick="startEdit()" class="btn btn-primary px-4 me-2">
                        전체 규정 설정
                    </button>
                    <form method="post" action="${pageContext.request.contextPath}/grade/admin/rule-global/init"
                          class="d-inline">
                        <button type="submit" class="btn btn-warning px-4 me-2">
                            기본 규정 초기화
                        </button>
                    </form>
                </div>

                <div id="totalDisplay" class="alert alert-light mt-3 mb-0" style="display:none;">
                    <span id="validationMessage" class="fw-bold"></span>
                </div>
            </div>
        </section>
    </div>
</main>

<%@ include file="/WEB-INF/views/components/footer.jsp" %>

<!-- CSS -->
<style>
    .display-mode {
        display: inline-block !important;
        padding: 8px 12px;
        background-color: #f8f9fa;
        border: 1px solid #dee2e6;
        border-radius: 4px;
        min-width: 80px;
        text-align: center;
        font-weight: 500;
    }
    
    .edit-mode {
        display: none !important;
    }
    
    .edit-mode.active {
        display: inline-block !important;
    }
</style>

<!-- JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<c:url value='/js/common-ui.js'/>"></script>

<script>
    let originalValues = {};

    function startEdit() {
        originalValues = {};
        document.querySelectorAll('.display-mode').forEach(span => {
            originalValues[span.dataset.alphabet] = span.textContent;
        });
        
        // 편집 모드로 전환
        document.querySelectorAll('.display-mode').forEach(span => span.style.display = 'none');
        document.querySelectorAll('.edit-mode').forEach(input => {
            input.style.display = 'inline-block';
            input.classList.add('active');
        });

        document.getElementById('viewButtons').style.display = 'none';
        document.getElementById('editButtons').style.display = 'block';
        document.getElementById('totalDisplay').style.display = 'block';
        validateGradeOrder();

        document.querySelectorAll('.edit-mode').forEach(input => {
            input.addEventListener('input', validateGradeOrder);
        });
    }

    function cancelEdit() {
        document.querySelectorAll('.display-mode').forEach(span => {
            span.textContent = originalValues[span.dataset.alphabet];
        });
        
        // 조회 모드로 전환
        document.querySelectorAll('.display-mode').forEach(span => span.style.display = 'inline-block');
        document.querySelectorAll('.edit-mode').forEach(input => {
            input.style.display = 'none';
            input.classList.remove('active');
        });
        
        document.getElementById('editButtons').style.display = 'none';
        document.getElementById('viewButtons').style.display = 'block';
        document.getElementById('totalDisplay').style.display = 'none';
    }

    function validateGradeOrder() {
        const gradeOrder = ['A+', 'A', 'B+', 'B', 'C+', 'C', 'D'];
        const values = {};
        let isValidOrder = true;
        let message = '';

        document.querySelectorAll('.edit-mode').forEach(input => {
            const grade = input.name.replace('boundary_', '');
            values[grade] = parseFloat(input.value || 0);
        });

        for (let i = 0; i < gradeOrder.length - 1; i++) {
            const cur = gradeOrder[i];
            const next = gradeOrder[i + 1];
            if (values[cur] && values[next] && values[cur] >= values[next]) {
                isValidOrder = false;
                message = `${cur}(${values[cur]}%)는 ${next}(${values[next]}%)보다 작아야 합니다`;
                break;
            }
        }

        const msg = document.getElementById('validationMessage');
        const saveBtn = document.querySelector('#editButtons button.btn-success');

        if (isValidOrder) {
            msg.textContent = '✓ 정상';
            msg.style.color = 'green';
            saveBtn.disabled = false;
        } else {
            msg.textContent = '✗ ' + message;
            msg.style.color = 'red';
            saveBtn.disabled = true;
        }
    }

    function saveChanges() {
        const formData = new FormData();
        document.querySelectorAll('.edit-mode').forEach(input => formData.append(input.name, input.value));
        document.querySelectorAll('input[name^="alphabet_"]').forEach(input => formData.append(input.name, input.value));

        const token = localStorage.getItem("accessToken");
        fetch('${pageContext.request.contextPath}/api/grade/admin/global-rules/save', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: formData
        })
            .then(res => res.ok ? res.text() : Promise.reject('저장 실패'))
            .then(msg => {
                cancelEdit();
                location.reload();
                alert(msg || '저장이 완료되었습니다.');
            })
            .catch(err => {
                console.error(err);
                alert('저장 중 오류가 발생했습니다.');
            });
    }
</script>
</body>
</html>
