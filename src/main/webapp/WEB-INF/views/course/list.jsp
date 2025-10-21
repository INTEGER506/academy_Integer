<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>강의 목록</title>
</head>
<body>
<div>
    <h1>강의 목록</h1>

    <c:if test="${not empty message}">
        <div style="color:green;">${message}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div style="color:red;">${error}</div>
    </c:if>

    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px;">
        <div>
            <%-- 교수 전용: 강의 개설 버튼 --%>
            <sec:authorize access="hasRole('PROFESSOR')">
                <button type="button" class="btn btn-primary"
                        onclick="location.href='/courses/add'">
                    강의 개설
                </button>
            </sec:authorize>

            <sec:authorize access="hasRole('ADMIN')">
                <c:choose>
                    <c:when test="${requestDTO.searchType == 'candidates'}">
                        <button type="button" class="btn btn-secondary"
                                onclick="location.href='/courses'">
                            전체 강의 목록
                        </button>
                    </c:when>
                    <%-- 관리자 전용: 폐강 후보 목록 버튼 --%>
                    <c:otherwise>
                        <button type="button" class="btn btn-warning"
                                onclick="location.href='/courses?searchType=candidates'">
                            정원 미달 강의 폐강 후보 목록
                        </button>
                    </c:otherwise>
                </c:choose>
            </sec:authorize>

        </div>

        <%-- 페이지당 항목수 선택 --%>
        <div class="page-size-selector">
            <label for="pageSizeSelect">항목 수:</label>
            <select id="pageSizeSelect" onchange="changePageSize(this.value)">
                <option value="10" ${requestDTO.pageSize == 10 ? 'selected' : ''}>10개</option>
                <option value="20" ${requestDTO.pageSize == 20 ? 'selected' : ''}>20개</option>
                <option value="50" ${requestDTO.pageSize == 50 ? 'selected' : ''}>50개</option>
            </select>
        </div>
    </div>

    <%-- 관리자 전용 검색창 --%>
    <sec:authorize access="hasRole('ADMIN')">
        <form action="/courses" method="get" class="search-form" id="searchForm">
            <input type="hidden" name="pageSize" value="${requestDTO.pageSize}">
            <input type="hidden" name="page" value="1">

            <select name="searchType">
                <option value="">전체</option>
                <option value="subject" ${requestDTO.searchType == 'subject' ? 'selected' : ''}>과목명</option>
                <option value="professor" ${requestDTO.searchType == 'professor' ? 'selected' : ''}>교수명</option>
                <option value="dept" ${requestDTO.searchType == 'dept' ? 'selected' : ''}>학과명</option>
            </select>
            <input type="text" name="searchKeyword" value="${requestDTO.searchKeyword}" placeholder="검색어를 입력하세요">
            <button type="submit">검색</button>
            <button type="button" onclick="location.href='/courses'">초기화</button>
        </form>
    </sec:authorize>

    <c:choose>
        <c:when test="${not empty pageResult.data}">
            <table border="1" width="100%" style="border-collapse: collapse; text-align: center;">
                <thead>
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
                    <!-- 교수 전용 -->
                    <sec:authorize access="hasAnyRole('PROFESSOR')">
                        <th>관리</th>
                    </sec:authorize>
                    <!-- 관리자 전용 (폐강 후보 목록일 때만) -->
                    <sec:authorize access="hasRole('ADMIN')">
                        <c:if test="${requestDTO.searchType == 'candidates'}">
                            <th>관리</th>
                        </c:if>
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
                        <td><span>${course.status}</span></td>

                        <!-- 교수 전용: 수정/삭제 -->
                        <sec:authorize access="hasAnyRole('PROFESSOR')">
                            <td>
                                <button type="button" class="btn btn-warning btn-sm"
                                        onclick="location.href='/courses/edit/${course.id}'">수정
                                </button>
                                <button type="button" class="btn btn-danger btn-sm"
                                        onclick="deleteCourse(${course.id}, this)">삭제
                                </button>
                            </td>
                        </sec:authorize>

                        <!-- 관리자 전용: 폐강 (폐강 후보 목록일 때만) -->
                        <sec:authorize access="hasRole('ADMIN')">
                            <td>
                                <c:if test="${requestDTO.searchType == 'candidates' && course.status == 'OPEN' && course.numOfStudent lt 4}">
                                    <button type="button" class="btn btn-danger btn-sm"
                                            onclick="closeCourse(${course.id}, this)">폐강</button>
                                </c:if>
                            </td>
                        </sec:authorize>
                    </tr>
                </c:forEach>
                </tbody>
            </table>

            <!-- 페이지네이션 -->
            <div class="pagination" style="margin-top:20px; text-align:center;">
                <c:if test="${pageResult.hasPrevious}">
                    <button onclick="updateUrlParams({ page: ${pageResult.startPage - 1} })"
                            class="btn btn-secondary btn-sm">이전</button>
                </c:if>

                <c:forEach begin="${pageResult.startPage}" end="${pageResult.endPage}" var="num">
                    <button onclick="updateUrlParams({ page: ${num} })"
                            class="btn btn-sm ${pageResult.currentPage eq num ? 'btn-primary' : 'btn-outline-primary'}">
                            ${num}
                    </button>
                </c:forEach>

                <c:if test="${pageResult.hasNext}">
                    <button onclick="updateUrlParams({ page: ${pageResult.endPage + 1} })"
                            class="btn btn-secondary btn-sm">다음</button>
                </c:if>

                <p>총 ${pageResult.totalCount}개 강의</p>
            </div>
        </c:when>
        <c:otherwise>
            <p>등록된 강의가 없습니다.</p>
        </c:otherwise>
    </c:choose>
</div>

<script>
    // 강의 삭제 (비동기)
    async function deleteCourse(courseId, button) {
        if (!confirm("정말 이 강의를 삭제하시겠습니까?")) return;

        const token = localStorage.getItem("accessToken");

        try {
            const response = await fetch("/api/courses/" + courseId, {
                method: "DELETE",
                headers: { "Authorization": `Bearer ${token}` }
            });

            const message = await response.text();

            if (!response.ok) throw new Error(message || "삭제 실패");

            alert(message || "강의가 삭제되었습니다.");

            // 화면 즉시 반영
            const row = document.getElementById(`row-${courseId}`);
            if (row) row.remove();
            else location.reload(); // 안전장치

        } catch (err) {
            if (err.message.includes("401")) {
                alert("로그인 세션이 만료되었습니다. 다시 로그인해주세요.");
                window.location.href = "/auth/login";
            } else {
                alert("삭제 중 오류 발생: " + err.message);
            }
        }
    }

    // 강의 폐강 (비동기)
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

    // URL 파라미터 처리
    function updateUrlParams(params) {
        const url = new URL(window.location.href);
        const searchKeyword = url.searchParams.get('searchKeyword');
        const searchType = url.searchParams.get('searchType');
        const pageSize = url.searchParams.get('pageSize');

        url.searchParams.set('searchKeyword', params.searchKeyword ?? (searchKeyword || ''));
        url.searchParams.set('searchType', params.searchType ?? (searchType || ''));
        url.searchParams.set('pageSize', params.pageSize ?? (pageSize || 10));

        if (params.page !== undefined) url.searchParams.set('page', params.page);

        window.location.href = url.toString();
    }

    function changePageSize(size) { updateUrlParams({ pageSize: size, page: 1 }); }
    function movePage(pageNumber) { updateUrlParams({ page: pageNumber }); }
    function changeSort(sortField) { updateUrlParams({ sort: sortField, page: 1 }); }
</script>
</body>
</html>
