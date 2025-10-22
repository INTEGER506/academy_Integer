<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>점수 분배 비율 설정</title>

    <!-- 공통 리소스 -->
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;700&family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>

<body class="bg-page">
<%@ include file="/WEB-INF/views/components/header.jsp" %>

<main class="py-4">
    <div class="container-1200 d-flex gap-24">
        <%@ include file="/WEB-INF/views/components/sidebar.jsp" %>

        <!--  메인 콘텐츠 -->
        <section class="flex-1 d-flex flex-column gap-24">

            <!-- 상단 내비게이션 -->
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h2 class="fw-bold text-navy mb-0"> 점수 분배 비율 설정</h2>
                <div>
                    <a href="/" class="btn btn-outline-secondary me-2">
                        <i class="bi bi-house-door"></i> 메인페이지
                    </a>
                    <a href="${pageContext.request.contextPath}/grade/professor/system-list" class="btn btn-outline-primary">
                        ← 목록으로
                    </a>
                </div>
            </div>

            <!-- 과목 정보 -->
            <c:if test="${subjectName != null}">
                <div class="card-white p-20">
                    <h5 class="text-navy fw-bold mb-2"> 과목: ${subjectName}</h5>
                    <p class="text-gray-600 small mb-3">이 과목의 점수 분배 비율을 설정합니다.</p>

                    <!-- 현재 GradeSystem -->
                    <c:choose>
                        <c:when test="${gs != null && gs.id != null}">
                            <div class="bg-light border rounded p-3">
                                <h6 class="fw-bold text-navy mb-2"> 현재 설정된 비율</h6>
                                <div class="d-flex flex-wrap gap-3">
                                    <span class="badge bg-success">중간고사: ${gs.midExamRatio.intValue()}%</span>
                                    <span class="badge bg-primary">기말고사: ${gs.finalExamRatio.intValue()}%</span>
                                    <span class="badge bg-warning text-dark">과제: ${gs.assignmentRatio.intValue()}%</span>
                                    <span class="badge bg-secondary">출석: ${gs.attendanceRatio.intValue()}%</span>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-warning small mt-2">
                                 아직 이 과목의 점수 분배 비율이 설정되지 않았습니다. 아래에서 새로 설정해주세요.
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:if>

            <!-- 현재 과목 성적 규정 -->
            <div class="card-white p-20">
                <h5 class="fw-bold text-navy mb-3"> 현재 과목 성적 규정 (상위 누적 비율)</h5>
                <p class="text-gray-600 small mb-3">
                    이 과목에 적용되는 학점 규정입니다. 커스텀 규정이 없으면 글로벌 규정이 적용됩니다.
                </p>

                <div class="table-responsive">
                    <table class="table table-hover text-center align-middle">
                        <thead class="table-navy text-white">
                            <tr>
                                <th>등급</th>
                                <th>상위 누적 비율 (%)</th>
                                <th>설명</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:set var="rulesToDisplay" value="${subjectRules != null && not empty subjectRules ? subjectRules : globalRules}"/>
                            <c:if test="${empty rulesToDisplay}">
                                <tr><td colspan="3" class="text-gray-600 py-3">규정 정보가 없습니다.</td></tr>
                            </c:if>
                            <c:forEach var="rule" items="${rulesToDisplay}">
                                <tr>
                                    <td class="fw-bold">${rule.alphabet}</td>
                                    <td><fmt:formatNumber value="${rule.boundary}" pattern="0"/>%</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <p class="text-muted small mt-2">
                    총점 30점 이하인 경우 자동으로 F 학점이 부여됩니다.
                </p>
            </div>

            <!-- 점수 비율 설정 폼 -->
            <div class="card-white p-20">
                <h5 class="fw-bold text-success mb-3"> 점수 비율 설정</h5>

                <form method="post" action="${pageContext.request.contextPath}/grade/professor/system/edit">
                    <input type="hidden" name="id" value="${gs.id}"/>
                    <input type="hidden" name="courseId" value="${param.courseId != null ? param.courseId : gs.courseId}"/>
                    <input type="hidden" name="subjectId" value="${param.subjectId}"/>
                    <input type="hidden" name="professorId" value="${professorId}"/>

                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label class="form-label fw-bold">중간고사 비율 (%)</label>
                            <input type="number" name="midExamRatio" class="form-control" min="0" max="100"
                                   value="${gs.midExamRatio != null ? gs.midExamRatio : 30}" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">기말고사 비율 (%)</label>
                            <input type="number" name="finalExamRatio" class="form-control" min="0" max="100"
                                   value="${gs.finalExamRatio != null ? gs.finalExamRatio : 40}" required>
                        </div>
                    </div>

                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label class="form-label fw-bold">과제 비율 (%)</label>
                            <input type="number" name="assignmentRatio" class="form-control" min="0" max="100"
                                   value="${gs.assignmentRatio != null ? gs.assignmentRatio : 20}" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">출석 비율 (%)</label>
                            <input type="number" name="attendanceRatio" class="form-control" min="0" max="100"
                                   value="${gs.attendanceRatio != null ? gs.attendanceRatio : 10}" required>
                        </div>
                    </div>

                    <div class="alert alert-light border mt-3" id="totalDisplay">
                        <strong>총합: <span id="totalSum">0</span>%</strong>
                        <span id="validationMessage" class="ms-2 fw-bold"></span>
                    </div>

                    <div class="mt-3">
                        <button type="submit" id="submitBtn" class="btn btn-outline-success" disabled>
                             저장
                        </button>
                        <a href="${pageContext.request.contextPath}/grade/professor/system-list?professorId=${professorId}"
                           class="btn btn-outline-secondary">← 목록으로</a>
                    </div>
                </form>
            </div>
        </section>
    </div>
</main>

<%@ include file="/WEB-INF/views/components/footer.jsp" %>

<!--  공통 JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<c:url value='/js/common-ui.js'/>"></script>

<script>
    //  실시간 총합 계산 및 검증
    function updateTotal() {
        const midExam = Number(document.querySelector('input[name="midExamRatio"]').value || 0);
        const finalExam = Number(document.querySelector('input[name="finalExamRatio"]').value || 0);
        const assignment = Number(document.querySelector('input[name="assignmentRatio"]').value || 0);
        const attendance = Number(document.querySelector('input[name="attendanceRatio"]').value || 0);

        const total = midExam + finalExam + assignment + attendance;
        document.getElementById('totalSum').textContent = total;

        const validationMessage = document.getElementById('validationMessage');
        const submitBtn = document.getElementById('submitBtn');

        if (total === 100) {
            validationMessage.textContent = '✓ 정상';
            validationMessage.style.color = 'green';
            submitBtn.disabled = false;
        } else {
            validationMessage.textContent = '✗ 총합이 100%가 아닙니다';
            validationMessage.style.color = 'red';
            submitBtn.disabled = true;
        }
    }

    document.querySelectorAll('input[type="number"]').forEach(input => {
        input.addEventListener('input', updateTotal);
    });

    // 초기 합계 계산
    updateTotal();
</script>

</body>
</html>
