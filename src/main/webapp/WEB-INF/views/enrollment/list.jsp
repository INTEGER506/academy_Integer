<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>학사정보관리시스템 - 수강 신청</title>

    <!-- ✅ 공통 스타일 -->
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet"/>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>"/>
    <link rel="icon" href="data:,">
</head>

<body class="bg-page">
<%@ include file="/WEB-INF/views/components/header.jsp" %>

<main class="py-4">
  <div class="container-1200 d-flex gap-24">
    <%@ include file="/WEB-INF/views/components/sidebar.jsp" %>

    <!-- ✅ 메인 콘텐츠 -->
    <section class="flex-1">
      <div class="card-white p-4">

        <h3 class="fw-700 text-navy mb-4 d-flex align-items-center gap-2">
          <i class="bi bi-bookmark-check"></i> 수강 신청
        </h3>

        <!-- ✅ 메시지 영역 -->
        <div id="message-area" class="text-center mb-3">
          <c:if test="${not empty message}">
            <p class="text-success fw-600">${message}</p>
          </c:if>
          <c:if test="${not empty error}">
            <p class="text-danger fw-600">${error}</p>
          </c:if>
        </div>

        <!-- ✅ 현재 학점 -->
        <p id="creditStatus" class="fw-500 mb-3">
          <b>현재 신청 학점:</b> ${currentCredits}학점
          <span class="text-gray-600">(최대 ${maxCredits}학점)</span>
        </p>

        <!-- ✅ 검색 폼 -->
        <form action="/enrollments" method="get" class="d-flex gap-2 mb-3 align-items-center flex-wrap">
          <select name="searchType" class="form-select form-select-sm w-auto">
            <option value="all" ${param.searchType == 'all' ? 'selected' : ''}>전체</option>
            <option value="subject" ${param.searchType == 'subject' ? 'selected' : ''}>과목명</option>
            <option value="professor" ${param.searchType == 'professor' ? 'selected' : ''}>교수명</option>
            <option value="dept" ${param.searchType == 'dept' ? 'selected' : ''}>학과명</option>
          </select>
          <input type="text" name="searchKeyword" value="${param.searchKeyword}"
                 class="form-control form-control-sm w-auto" placeholder="검색어 입력"/>
          <button type="submit" class="btn btn-navy btn-sm rounded-pill">
            <i class="bi bi-search"></i> 검색
          </button>
        </form>

        <!-- ✅ 강의 목록 -->
        <div class="table-responsive">
          <table class="table table-hover align-middle text-center">
            <thead class="table-light">
              <tr>
                <th>ID</th>
                <th>학과</th>
                <th>과목명</th>
                <th>교수명</th>
                <th>학점</th>
                <th>시간</th>
                <th>강의실</th>
                <th>현재인원</th>
                <th>정원</th>
                <th>신청</th>
              </tr>
            </thead>
            <tbody id="courseBody">
              <c:forEach var="course" items="${courseList}">
                <tr data-id="${course.id}" data-credit="${course.credit}">
                  <td>${course.id}</td>
                  <td>${course.deptName}</td>
                  <td class="fw-500 text-navy">${course.subjectName}</td>
                  <td>${course.professorName}</td>
                  <td>${course.credit}</td>
                  <td>${course.simpleDayTime}</td>
                  <td>${course.simplePlace}</td>
                  <td class="num">${course.numOfStudent}</td>
                  <td>${course.capacity}</td>
                  <td>
                    <button class="btn btn-sm rounded-pill enroll-btn ${course.isEnrolled ? 'btn-secondary' : 'btn-outline-navy'}"
                            ${course.isEnrolled ? 'disabled' : ''}>
                      ${course.isEnrolled ? '신청완료' : '신청'}
                    </button>
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>

        <!-- ✅ 페이지네이션 -->
        <div class="text-center mt-3">
          <c:if test="${pageResponse != null}">
            <c:forEach var="i" begin="${pageResponse.startPage}" end="${pageResponse.endPage}">
              <c:choose>
                <c:when test="${i == pageResponse.currentPage}">
                  <strong class="text-navy">[${i}]</strong>
                </c:when>
                <c:otherwise>
                  <a class="text-decoration-none text-dark mx-1"
                     href="?page=${i}&searchType=${param.searchType}&searchKeyword=${param.searchKeyword}">[${i}]</a>
                </c:otherwise>
              </c:choose>
            </c:forEach>
          </c:if>
        </div>
      </div>
    </section>
  </div>
</main>

<%@ include file="/WEB-INF/views/components/footer.jsp" %>

<!-- ✅ 스크립트 -->
<script src="<c:url value='/vendor/jquery/jquery-3.7.1.min.js'/>"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
  let currentCredits = ${currentCredits};
  const maxCredits = ${maxCredits};

  // ✅ 학점 갱신
  async function refreshCredits() {
    const token = localStorage.getItem("accessToken");
    try {
      const res = await fetch("/api/enrollments/credits", {
        headers: { "Authorization": `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        currentCredits = data.currentCredits;
        document.getElementById("creditStatus").innerHTML =
          `<b>현재 신청 학점:</b> ${currentCredits}학점 <span class="text-gray-600">(최대 ${data.maxCredits}학점)</span>`;
      } else if (res.status === 401) {
        alert("세션이 만료되었습니다. 다시 로그인해주세요.");
        window.location.href = "/auth/login";
      }
    } catch (err) {
      console.error("학점 갱신 오류:", err);
    }
  }

  // ✅ 수강 신청
  document.querySelectorAll('.enroll-btn').forEach(btn => {
    btn.addEventListener('click', async e => {
      const token = localStorage.getItem("accessToken");
      const row = e.target.closest('tr');
      const courseId = row.dataset.id;
      const numCell = row.querySelector('.num');

      if (!confirm("이 강의를 신청하시겠습니까?")) return;

      try {
        const response = await fetch("/api/enrollments/" + courseId, {
          method: "POST",
          headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
          }
        });

        if (!response.ok) {
          const errText = await response.text();
          alert("오류: " + errText);
          return;
        }

        e.target.disabled = true;
        e.target.textContent = "신청완료";
        e.target.classList.remove("btn-outline-navy");
        e.target.classList.add("btn-secondary");

        let nowNum = parseInt(numCell.textContent) || 0;
        numCell.textContent = nowNum + 1;

        await refreshCredits();
        alert("수강 신청이 완료되었습니다.");
        setTimeout(() => location.reload(), 300);
      } catch (err) {
        alert("네트워크 오류: " + err.message);
      }
    });
  });
</script>
</body>
</html>
