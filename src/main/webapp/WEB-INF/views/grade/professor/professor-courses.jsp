<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>성적 관리 - 내 강의 목록</title>

    <!-- 공통 리소스 -->
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;700&family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>

<body class="bg-page">
<%@ include file="/WEB-INF/views/components/header.jsp" %>

<main class="py-5">
    <div class="container-1200 d-flex gap-24">
        <%@ include file="/WEB-INF/views/components/sidebar.jsp" %>

        <!-- ✅ 본문 콘텐츠 -->
        <section class="flex-1 d-flex flex-column gap-24">

            <!-- 제목 + 메인버튼 -->
            <div class="d-flex justify-content-between align-items-center">
                <h3 class="text-navy fw-bold mb-0">
                    <i class="bi bi-mortarboard"></i> 성적 관리 - 내 강의 목록
                </h3>
                <a href="/" class="btn btn-outline-secondary rounded-pill px-3">
                    <i class="bi bi-house-door"></i> 메인페이지
                </a>
            </div>

            <p class="text-gray-600 small">
                강의를 선택하여 해당 강의의 수강생 목록을 확인하고 성적을 관리할 수 있습니다.
            </p>

            <!-- 🔍 검색 바 -->
            <div class="card-white p-4">
                <jsp:include page="/WEB-INF/views/common/searchBar.jsp">
                    <jsp:param name="formAction" value="${pageContext.request.contextPath}/grade/professor/courses"/>
                    <jsp:param name="optionValues" value="subject|professor"/>
                    <jsp:param name="optionLabels" value="과목명|교수명"/>
                    <jsp:param name="pageSizeOptions" value="10|20|50"/>
                    <jsp:param name="placeHolder" value="과목명 또는 교수명 입력"/>
                    <jsp:param name="keep" value="professorId=${professorId}"/>
                    <jsp:param name="req" value="${req}"/>
                </jsp:include>
            </div>

            <!-- 📋 강의 목록 -->
            <div class="card-white p-4">
                <div class="table-responsive">
                    <table class="table table-hover align-middle text-center">
                        <thead class="table-navy text-white">
                            <tr>
                                <th>ID</th>
                                <th>과목명</th>
                                <th>학점</th>
                                <th>강의시간</th>
                                <th>강의실</th>
                                <th>수강인원</th>
                                <th>정원</th>
                                <th>상태</th>
                                <th>관리</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="course" items="${result.data}">
                                <tr>
                                    <td>${course.COURSEID}</td>
                                    <td class="fw-600">${course.SUBJECTNAME}</td>
                                    <td>${course.CREDIT}학점</td>
                                    <td>${course.DAYOFWEEK} ${course.TIME}</td>
                                    <td>${course.PLACE}</td>
                                    <td>
                                        <span class="badge bg-info text-dark">${course.NUMOFSTUDENT}명</span>
                                    </td>
                                    <td>${course.CAPACITY}명</td>
                                    <td>
                                        <span class="badge ${course.STATUS == 'OPEN' ? 'bg-success' : 'bg-secondary'}">
                                            ${course.STATUS}
                                        </span>
                                    </td>

                                    <!-- ✅ 수강생 관리 버튼 -->
                                    <c:url var="studentListUrl" value="/grade/professor/list">
                                        <c:param name="courseId" value="${course.COURSEID}" />
                                        <c:param name="subjectId" value="${course.SUBJECTID}" />
                                        <c:param name="professorId" value="${professorId}" />
                                    </c:url>
                                    <td>
                                        <a href="${studentListUrl}" class="btn btn-sm btn-outline-navy rounded-pill px-3">
                                            <i class="bi bi-people"></i> 수강생 관리
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>

                            <!-- ⚠️ 데이터 없음 -->
                            <c:if test="${empty result.data}">
                                <tr>
                                    <td colspan="9" class="text-center py-4 text-gray-500">
                                        <i class="bi bi-journal-x"></i> 개설된 강의가 없습니다.
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- 📄 페이지네이션 -->
            <div>
                <jsp:include page="/WEB-INF/views/common/page.jsp">
                    <jsp:param name="baseUrl" value="${pageContext.request.contextPath}/grade/professor/courses"/>
                    <jsp:param name="req" value="${req}"/>
                    <jsp:param name="result" value="${result}"/>
                    <jsp:param name="keepParams" value="professorId=${professorId}"/>
                </jsp:include>
            </div>

            <!-- 💡 안내 -->
            <div class="alert alert-light border">
                <h5 class="fw-bold mb-2"><i class="bi bi-info-circle"></i> 사용 안내</h5>
                <ul class="small mb-0 text-gray-700">
                    <li><strong>수강생 관리:</strong> “수강생 관리” 버튼을 클릭하여 해당 강의의 수강생 목록을 확인하고 성적을 관리합니다.</li>
                    <li><strong>강의 수정:</strong> 관리 기능에서 강의 정보를 편집할 수 있습니다.</li>
                    <li><strong>점수 분배 설정:</strong> “글로벌 규정” 또는 “과목별 규정 관리” 메뉴에서 조정 가능합니다.</li>
                </ul>
            </div>
        </section>
    </div>
</main>

<%@ include file="/WEB-INF/views/components/footer.jsp" %>

<!-- JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<c:url value='/js/common-ui.js'/>"></script>
</body>
</html>
