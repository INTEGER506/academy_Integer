<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>점수 분배 비율 설정 목록</title>

    <!--  공통 리소스 -->
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

            <!-- 상단 헤더 -->
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h2 class="fw-bold text-navy mb-0"> 점수 분배 비율 설정</h2>
                <a href="/" class="btn btn-outline-secondary">
                    <i class="bi bi-house-door"></i> 메인페이지
                </a>
            </div>

            <p class="text-gray-600 small mb-3">
                각 강의별로 중간고사, 기말고사, 과제, 출석의 점수 비율을 확인하고 수정할 수 있습니다.
            </p>

            <!-- 검색 바 -->
            <div class="card-white p-20">
                <jsp:include page="/WEB-INF/views/common/searchBar.jsp">
                    <jsp:param name="formAction"      value="${pageContext.request.contextPath}/grade/professor/system-list"/>
                    <jsp:param name="optionValues"    value="courseName"/>
                    <jsp:param name="optionLabels"    value="강의명"/>
                    <jsp:param name="pageSizeOptions" value="10|20|50"/>
                    <jsp:param name="placeHolder"     value="강의명 검색"/>
                    <jsp:param name="keep"            value="professorId=${professorId}"/>
                    <jsp:param name="req"             value="${req}"/>
                </jsp:include>
            </div>

            <!-- 점수 비율 테이블 -->
            <div class="card-white p-20">
                <div class="table-responsive">
                    <table class="table table-hover align-middle text-center">
                        <thead class="table-navy text-white">
                            <tr>
                                <th>#</th>
                                <th>강의명</th>
                                <th>중간고사</th>
                                <th>기말고사</th>
                                <th>과제</th>
                                <th>출석</th>
                                <th>총합</th>
                                <th>관리</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="course" items="${result.data}" varStatus="st">
                                <tr>
                                    <td>${(result.currentPage - 1) * result.pageSize + st.index + 1}</td>
                                    <td class="fw-600 text-start ps-3">${course.SUBJECTNAME}</td>
                                    <td><span class="text-success fw-500">${course.gradeSystem != null && course.gradeSystem.midExamRatio != null ? course.gradeSystem.midExamRatio.intValue() : 30}%</span></td>
                                    <td><span class="text-primary fw-500">${course.gradeSystem != null && course.gradeSystem.finalExamRatio != null ? course.gradeSystem.finalExamRatio.intValue() : 40}%</span></td>
                                    <td><span class="text-warning fw-500">${course.gradeSystem != null && course.gradeSystem.assignmentRatio != null ? course.gradeSystem.assignmentRatio.intValue() : 20}%</span></td>
                                    <td><span class="text-secondary fw-500">${course.gradeSystem != null && course.gradeSystem.attendanceRatio != null ? course.gradeSystem.attendanceRatio.intValue() : 10}%</span></td>
                                    <td><span class="fw-bold text-navy">100%</span></td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/grade/professor/system/edit?professorId=${professorId}&courseId=${course.COURSEID}&subjectId=${course.SUBJECTID}${course.gradeSystem != null && course.gradeSystem.id != null ? '&id=' : ''}${course.gradeSystem != null ? course.gradeSystem.id : ''}"
                                           class="btn btn-sm btn-outline-navy">
                                             수정
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>

                            <c:if test="${empty result.data}">
                                <tr>
                                    <td colspan="8" class="py-5 text-gray-500 text-center">
                                        <i class="bi bi-journal-x fs-1 d-block mb-2"></i>
                                        <div>등록된 강의가 없습니다.</div>
                                        <small class="text-muted"> 강의 관리 메뉴에서 먼저 강의를 등록해주세요.</small>
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- 페이지네이션 -->
            <div>
                <jsp:include page="/WEB-INF/views/common/page.jsp">
                    <jsp:param name="baseUrl"    value="${pageContext.request.contextPath}/grade/professor/system-list"/>
                    <jsp:param name="req"        value="${req}"/>
                    <jsp:param name="result"     value="${result}"/>
                    <jsp:param name="keepParams" value="${keepParams}"/>
                </jsp:include>
            </div>

            <!-- 하단 버튼 -->
            <div class="text-center mt-3">
                <a href="/" class="btn btn-outline-secondary me-2"> 메인페이지</a>
            </div>
        </section>
    </div>
</main>

<%@ include file="/WEB-INF/views/components/footer.jsp" %>

<!-- 공통 JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<c:url value='/js/common-ui.js'/>"></script>
</body>
</html>
