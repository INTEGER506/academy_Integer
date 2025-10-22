<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <title>강의 관리 - 목록</title>
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
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h4 class="fw-bold">강의 목록</h4>

            <div class="d-flex gap-2">
                <sec:authorize access="hasRole('ROLE_PROFESSOR')">
                    <button type="button" class="btn btn-primary btn-sm"
                            onclick="location.href='/courses/add'">
                        <i class="bi bi-plus-circle"></i> 강의 개설
                    </button>
                </sec:authorize>

                <sec:authorize access="hasRole('ROLE_ADMIN')">
                    <c:choose>
                        <c:when test="${requestDTO.searchType == 'candidates'}">
                            <button type="button" class="btn btn-secondary btn-sm"
                                    onclick="location.href='/courses'">
                                <i class="bi bi-list"></i> 전체 강의 목록
                            </button>
                        </c:when>
                        <c:otherwise>
                            <button type="button" class="btn btn-warning btn-sm"
                                    onclick="location.href='/courses?searchType=candidates'">
                                <i class="bi bi-exclamation-triangle"></i> 폐강 후보 목록
                            </button>
                        </c:otherwise>
                    </c:choose>
                </sec:authorize>
            </div>
        </div>

        <!-- ✅ 관리자 전용 검색창 -->
        <sec:authorize access="hasRole('ROLE_ADMIN')">
            <form action="/courses" method="get" class="d-flex align-items-center gap-2 mb-3">
                <input type="hidden" name="pageSize" value="${requestDTO.pageSize}">
                <input type="hidden" name="page" value="1">

                <select name="searchType" class="form-select form-select-sm w-auto">
                    <option value="">전체</option>
                    <option value="subject" ${requestDTO.searchType == 'subject' ? 'selected' : ''}>과목명</option>
                    <option value="professor" ${requestDTO.searchType == 'professor' ? 'selected' : ''}>교수명</option>
                    <option value="dept" ${requestDTO.searchType == 'dept' ? 'selected' : ''}>학과명</option>
                </select>
                <input type="text" name="searchKeyword" value="${requestDTO.searchKeyword}"
                       class="form-control form-control-sm w-200" placeholder="검색어 입력">
                <button type="submit" class="btn btn-outline-primary btn-sm">검색</button>
                <button type="button" class="btn btn-outline-secondary btn-sm" onclick="location.href='/courses'">초기화</button>
            </form>
        </sec:authorize>

        <!-- ✅ 목록 테이블 -->
        <c:choose>
            <c:when test="${not empty pageResult.data}">
                <table class="table table-bordered table-hover text-center align-middle">
                    <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>학과</th>
                        <th>과목</th>
                        <th>담당교수</th>
                        <th>학점</th>
                        <th>강의시간</th>
                        <th>강의실</th>
                        <th>수강인원</th>
                        <th>정원</th>
                        <th>상태</th>

                        <sec:authorize access="hasAnyRole('ROLE_PROFESSOR','ROLE_ADMIN')">
                            <th>관리</th>
                        </sec:authorize>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="course" items="${pageResult.data}">
                        <tr id="row-${course.id}">
                            <td>${course.id}</td>
                            <td>${course.deptName}</td>
                            <td>${course.subjectName}</td>
                            <td>${course.professorName}</td>
                            <td>${course.credit}</td>
                            <td>${course.simpleDayTime}</td>
                            <td>${course.simplePlace}</td>
                            <td>${course.numOfStudent}</td>
                            <td>${course.capacity}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${course.status == 'OPEN'}">
                                        <span class="badge bg-success">개설</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-secondary">폐강</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <!-- 교수 전용 수정/삭제 -->
                            <sec:authorize access="hasRole('ROLE_PROFESSOR')">
                                <td>
                                    <button class="btn btn-warning btn-sm"
                                            onclick="location.href='/courses/edit/${course.id}'">
                                        수정
                                    </button>
                                    <button class="btn btn-danger btn-sm"
                                            onclick="deleteCourse(${course.id}, this)">
                                        삭제
                                    </button>
                                </td>
                            </sec:authorize>

                            <!-- 관리자 전용 폐강 -->
                            <sec:authorize access="hasRole('ROLE_ADMIN')">
                                <td>
                                    <c:if test="${course.status == 'OPEN' && course.numOfStudent lt 4}">
                                        <button class="btn btn-danger btn-sm"
                                                onclick="closeCourse(${course.id}, this)">폐강</button>
                                    </c:if>
                                </td>
                            </sec:authorize>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>

                <!-- ✅ 페이지네이션 -->
                <div class="d-flex justify-content-center align-items-center gap-2 mt-3">
                    <c:if test="${pageResult.hasPrevious}">
                        <button class="btn btn-outline-secondary btn-sm"
                                onclick="updateUrlParams({ page: ${pageResult.startPage - 1} })">이전</button>
                    </c:if>

                    <c:forEach begin="${pageResult.startPage}" end="${pageResult.endPage}" var="num">
                        <button class="btn btn-sm ${pageResult.currentPage eq num ? 'btn-primary' : 'btn-outline-primary'}"
                                onclick="updateUrlParams({ page: ${num} })">${num}</button>
                    </c:forEach>

                    <c:if test="${pageResult.hasNext}">
                        <button class="btn btn-outline-secondary btn-sm"
                                onclick="updateUrlParams({ page: ${pageResult.endPage + 1} })">다음</button>
                    </c:if>
                </div>

                <p class="text-end text-gray-600 small mt-2">총 ${pageResult.totalCount}개 강의</p>
            </c:when>

            <c:otherwise>
                <div class="alert alert-light text-center">등록된 강의가 없습니다.</div>
            </c:otherwise>
        </c:choose>
    </section>
</main>

<jsp:include page="/WEB-INF/views/components/footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<c:url value='/vendor/jquery/jquery-3.7.1.min.js'/>"></script>

<script>
    // 강의 삭제
    async function deleteCourse(courseId, btn) {
        if (!confirm("정말 이 강의를 삭제하시겠습니까?")) return;
        const token = localStorage.getItem("accessToken");
        // try {
            const res = await fetch("/api/courses/" + courseId, {
                method: "DELETE",
                headers: { "Authorization": `Bearer ${token}` }
            });
            if (!res.ok) throw new Error(await res.text());
            alert("강의가 삭제되었습니다.");
            document.getElementById(`row-${courseId}`).remove();
        // } catch (e) {
        //     alert("삭제 실패: " + e.message);
        // }
    }

    // 강의 폐강
    async function closeCourse(courseId, button) {
        if (!confirm("정말로 이 강의를 폐강하시겠습니까?")) return;

        const token = localStorage.getItem("accessToken");

        try {
            const response = await fetch("/api/courses/close/" + courseId, {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${token}`
                }
            });

            const message = await response.text();

            if (!response.ok) throw new Error(message || "폐강 실패");

            alert(message || "폐강이 완료되었습니다.");

            button.disabled = true;
            button.textContent = "폐강됨";
            button.style.backgroundColor = "#999";

            const row = document.getElementById(`row-${courseId}`);
            if (row) {
                row.style.backgroundColor = "#f8d7da";
            } else {
                console.warn(`row-${courseId} 요소를 찾을 수 없습니다.`);
            }

        } catch (error) {
            if (error.message.includes("401")) {
                alert("로그인 세션이 만료되었습니다. 다시 로그인해주세요.");
                window.location.href = "/auth/login";
            } else {
                alert("폐강 실패: " + error.message);
            }
        }
    }

    // URL 업데이트
    function updateUrlParams(params) {
        const url = new URL(window.location.href);
        for (const key in params) {
            if (params[key] !== undefined)
                url.searchParams.set(key, params[key]);
        }
        window.location.href = url.toString();
    }
</script>
</body>
</html>
