<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>과목별 규정 목록 - 학사정보관리시스템</title>

    <!-- 폰트 / 부트스트랩 / 공통 스타일 -->
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

            <!-- 상단 제목 및 내비 -->
            <div class="d-flex align-items-center justify-content-between">
                <div>
                    <a href="/" class="btn btn-outline-secondary">
                        <i class="bi bi-house-door"></i> 메인페이지
                    </a>
                    <a href="${pageContext.request.contextPath}/grade/admin/rule-global" class="btn btn-outline-primary ms-2">
                        ← 글로벌 규정 보기
                    </a>
                </div>
                <h2 class="fw-bold text-navy mb-0">과목별 규정 목록</h2>
            </div>

            <p class="text-gray-600 small">
                각 과목별로 커스텀 성적 분배 비율을 설정할 수 있습니다. (상위 누적 비율 기준)
            </p>

            <!-- 검색바 -->
            <div class="card-white p-20 mb-3">
                <jsp:include page="/WEB-INF/views/common/searchBar.jsp">
                    <jsp:param name="formAction" value="${pageContext.request.contextPath}/grade/admin/subject-rules"/>
                    <jsp:param name="optionValues" value="subject|id"/>
                    <jsp:param name="optionLabels" value="과목명|과목ID"/>
                    <jsp:param name="pageSizeOptions" value="5|10|20|50"/>
                    <jsp:param name="placeHolder" value="검색어를 입력하세요"/>
                    <jsp:param name="keep" value=""/>
                    <jsp:param name="req" value="${req}"/>
                </jsp:include>
            </div>

            <!-- 테이블 -->
            <div class="card-white p-20">
                <table class="table table-hover align-middle text-center">
                    <thead class="table-navy text-white">
                    <tr>
                        <th>과목ID</th>
                        <th>과목명</th>
                        <th>규정 타입</th>
                        <th>등급별 상위 누적 비율</th>
                        <th>작업</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty result.data}">
                            <tr>
                                <td colspan="5" class="py-4 text-center text-gray-600">
                                    등록된 과목이 없습니다.<br/>
                                    <span class="small text-muted">과목 관리 기능에서 과목을 먼저 등록해주세요.</span>
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="subject" items="${result.data}" varStatus="status">
                                <tr data-subject-id="${subject.subjectId}">
                                    <td>${subject.subjectId}</td>
                                    <td>${subject.subjectName}</td>
                                    <td>
                                        <span class="badge ${subject.ruleType == 'CUSTOM' ? 'bg-primary' : 'bg-secondary'}">
                                            ${subject.ruleType == 'CUSTOM' ? '커스텀' : '글로벌'}
                                        </span>
                                    </td>
                                    <td>
                                        <div class="rules-display">
                                            <div class="d-flex flex-wrap justify-content-center gap-2">
                                                <c:forEach var="grade" items="${['A+', 'A', 'B+', 'B', 'C+', 'C', 'D']}">
                                                    <div class="d-flex align-items-center gap-1">
                                                        <span class="fw-bold">${grade}</span>
                                                        <span class="badge bg-light text-dark border grade-percentage">
                                                            <c:choose>
                                                                <c:when test="${subject.ruleType == 'CUSTOM'}">
                                                                    <c:set var="foundValue" value="false" />
                                                                    <c:forEach var="rule" items="${subject.rules}">
                                                                        <c:if test="${rule.alphabet == grade}">
                                                                            <fmt:formatNumber value="${rule.boundary}" pattern="0"/>%
                                                                            <c:set var="foundValue" value="true" />
                                                                        </c:if>
                                                                    </c:forEach>
                                                                    <c:if test="${!foundValue}">
                                                                        <fmt:formatNumber value="${globalRules[grade]}" pattern="0"/>%
                                                                    </c:if>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <fmt:formatNumber value="${globalRules[grade]}" pattern="0"/>%
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </span>
                                                    </div>
                                                </c:forEach>
                                            </div>
                                            <div class="edit-controls mt-2" style="display:none;">
                                                <button onclick="saveInlineEdit(this)" class="btn btn-sm btn-success px-3" data-subject-id="${subject.subjectId}">저장</button>
                                                <button onclick="cancelInlineEdit(this)" class="btn btn-sm btn-secondary px-3" data-subject-id="${subject.subjectId}">취소</button>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <button onclick="startInlineEdit(this)" class="btn btn-sm btn-outline-primary me-2" data-subject-id="${subject.subjectId}">수정</button>
                                        <button onclick="resetToGlobal(this)" class="btn btn-sm btn-outline-secondary" data-subject-id="${subject.subjectId}">초기화</button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>

            <!-- 페이지네이션 -->
            <div>
                <jsp:include page="/WEB-INF/views/common/page.jsp">
                    <jsp:param name="baseUrl" value="${pageContext.request.contextPath}/grade/admin/subject-rules"/>
                    <jsp:param name="req" value="${req}"/>
                    <jsp:param name="result" value="${result}"/>
                    <jsp:param name="keepParams" value="${keepParams}"/>
                </jsp:include>
            </div>
        </section>
    </div>
</main>

<%@ include file="/WEB-INF/views/components/footer.jsp" %>

<!-- 공통 스크립트 -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<!-- 과목별 규정 관리 JavaScript -->
<script>
    const originalValues = {};
    let globalRules = {
        'A+': ${globalRules['A+'] != null ? globalRules['A+'] : 15.0},
        'A': ${globalRules['A'] != null ? globalRules['A'] : 30.0},
        'B+': ${globalRules['B+'] != null ? globalRules['B+'] : 50.0},
        'B': ${globalRules['B'] != null ? globalRules['B'] : 70.0},
        'C+': ${globalRules['C+'] != null ? globalRules['C+'] : 75.0},
        'C': ${globalRules['C'] != null ? globalRules['C'] : 80.0},
        'D': ${globalRules['D'] != null ? globalRules['D'] : 95.0}
    };

    // 인라인 편집 시작
    function startInlineEdit(button) {
        const subjectId = button.getAttribute('data-subject-id');
        const row = button.closest('tr');
        const rulesDisplay = row.querySelector('.rules-display');
        const editControls = row.querySelector('.edit-controls');
        
        // 원본 값 저장
        originalValues[subjectId] = {};
        const gradeElements = rulesDisplay.querySelectorAll('.grade-percentage');
        gradeElements.forEach(element => {
            const grade = element.previousElementSibling.textContent;
            originalValues[subjectId][grade] = element.textContent;
        });
        
        // 편집 모드로 전환
        gradeElements.forEach(element => {
            const grade = element.previousElementSibling.textContent;
            const currentValue = element.textContent.replace('%', '');
            element.innerHTML = `<input type="number" class="form-control form-control-sm" value="${currentValue}" min="0" max="100" style="width: 60px;">`;
        });
        
        // 버튼 표시/숨김
        button.style.display = 'none';
        editControls.style.display = 'block';
    }

    // 인라인 편집 취소
    function cancelInlineEdit(button) {
        const subjectId = button.getAttribute('data-subject-id');
        const row = button.closest('tr');
        const rulesDisplay = row.querySelector('.rules-display');
        const editControls = row.querySelector('.edit-controls');
        const editButton = row.querySelector('[onclick="startInlineEdit(this)"]');
        
        // 원본 값으로 복원
        const gradeElements = rulesDisplay.querySelectorAll('.grade-percentage');
        gradeElements.forEach(element => {
            const grade = element.previousElementSibling.textContent;
            element.textContent = originalValues[subjectId][grade];
        });
        
        // 버튼 표시/숨김
        editButton.style.display = 'inline-block';
        editControls.style.display = 'none';
    }

    // 인라인 편집 저장
    function saveInlineEdit(button) {
        const subjectId = button.getAttribute('data-subject-id');
        const row = button.closest('tr');
        const rulesDisplay = row.querySelector('.rules-display');
        const editControls = row.querySelector('.edit-controls');
        const editButton = row.querySelector('[onclick="startInlineEdit(this)"]');
        
        // 입력된 값들 수집
        const inputs = rulesDisplay.querySelectorAll('input[type="number"]');
        const params = new URLSearchParams();
        params.append('subjectId', subjectId);
        
        inputs.forEach(input => {
            const grade = input.closest('.d-flex').querySelector('.fw-bold').textContent;
            const value = parseFloat(input.value);
            if (!isNaN(value)) {
                params.append('boundary_' + grade, value.toString());
            }
        });
        
        // AJAX로 저장 (기존 REST API 활용)
        console.log('저장 요청 파라미터:', params.toString());
        const token = localStorage.getItem("accessToken");
        fetch('/api/grade/admin/subject-rules/save', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Authorization': `Bearer ${token}`
            },
            body: params.toString()
        })
        .then(response => {
            console.log('응답 상태:', response.status);
            return response.text();
        })
        .then(data => {
            console.log('서버 응답:', data);
            if (data === 'SUCCESS') {
                // 성공 시 화면 업데이트
                const gradeElements = rulesDisplay.querySelectorAll('.grade-percentage');
                const inputs = rulesDisplay.querySelectorAll('input[type="number"]');
                
                // 입력된 값들로 화면 업데이트
                inputs.forEach((input, index) => {
                    if (gradeElements[index]) {
                        gradeElements[index].textContent = input.value + '%';
                    }
                });
                
                // 규정 타입을 커스텀으로 변경
                const ruleTypeBadge = row.querySelector('.badge');
                if (ruleTypeBadge) {
                    ruleTypeBadge.textContent = '커스텀';
                    ruleTypeBadge.className = 'badge bg-primary';
                }
                
                // 버튼 표시/숨김
                editButton.style.display = 'inline-block';
                editControls.style.display = 'none';
                
                alert('저장되었습니다.');
            } else {
                alert('저장에 실패했습니다: ' + data);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('저장 중 오류가 발생했습니다.');
        });
    }

    // 글로벌 규정으로 초기화
    function resetToGlobal(button) {
        const subjectId = button.getAttribute('data-subject-id');
        console.log('초기화 요청 subjectId:', subjectId);
        
        if (confirm('이 과목의 규정을 글로벌 규정으로 초기화하시겠습니까?')) {
            // 기존 REST API 활용
            const token = localStorage.getItem("accessToken");
            fetch('/api/grade/admin/subject-rules/reset?subjectId=' + subjectId, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            })
            .then(response => {
                console.log('초기화 응답 상태:', response.status);
                return response.text();
            })
            .then(data => {
                console.log('초기화 서버 응답:', data);
                if (data === 'SUCCESS') {
                    location.reload();
                } else {
                    alert('초기화에 실패했습니다: ' + data);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('초기화 중 오류가 발생했습니다.');
            });
        }
    }
</script>

</body>
</html>
