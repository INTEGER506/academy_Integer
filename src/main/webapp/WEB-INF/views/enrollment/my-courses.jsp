<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>학사정보관리시스템 - 내 수강 목록</title>

  <!-- ✅ 공통 리소스 -->
  <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet"/>
  <link rel="stylesheet" href="<c:url value='/css/style.css'/>"/>
  <link rel="icon" href="data:,">

  <style>
    .cancel-btn {
      background-color: #f44336;
      color: white;
      border: none;
      padding: 6px 12px;
      border-radius: 6px;
      font-size: 0.9rem;
      transition: background-color 0.2s ease;
    }
    .cancel-btn:hover {
      background-color: #d32f2f;
    }
    .btn-back {
      background-color: #0F2940;
      color: white;
      border: none;
      border-radius: 6px;
      padding: 6px 16px;
      font-size: 0.9rem;
      transition: background-color 0.2s ease;
    }
    .btn-back:hover {
      background-color: #1a3b63;
    }
  </style>
</head>

<body class="bg-page">
<%@ include file="/WEB-INF/views/components/header.jsp" %>

<main class="py-4">
  <div class="container-1200 d-flex gap-24">
    <%@ include file="/WEB-INF/views/components/sidebar.jsp" %>

    <section class="flex-1">
      <div class="card-white p-4">

        <h3 class="fw-700 text-navy mb-4 d-flex align-items-center gap-2">
          <i class="bi bi-journal-bookmark"></i> 내 수강 목록
        </h3>

        <!-- ✅ 현재 신청 학점 -->
        <p id="creditStatus" class="fw-500 mb-3">
          <b>현재 신청 학점:</b> ${currentCredits}학점
          <span class="text-gray-600">(최대 ${maxCredits}학점)</span>
        </p>

        <!-- ✅ 강의 목록 -->
        <div class="table-responsive">
          <table class="table table-hover align-middle text-center">
            <thead class="table-light">
              <tr>
                <th>강의번호</th>
                <th>과목명</th>
                <th>담당 교수</th>
                <th>학점</th>
                <th>강의시간</th>
                <th>강의실</th>
                <th>수강취소</th>
              </tr>
            </thead>

            <tbody id="courseBody">
              <c:choose>
                <c:when test="${empty myCourses}">
                  <tr><td colspan="7" class="py-4 text-gray-500">현재 신청한 강의가 없습니다.</td></tr>
                </c:when>
                <c:otherwise>
                  <c:forEach var="course" items="${myCourses}">
                    <tr data-id="${course.id}" data-credit="${course.credit}">
                      <td>${course.id}</td>
                      <td class="fw-500 text-navy">${course.subjectName}</td>
                      <td>${course.professorName}</td>
                      <td>${course.credit}</td>
                      <td>${course.simpleDayTime}</td>
                      <td>${course.simplePlace}</td>
                      <td>
                        <button class="cancel-btn" onclick="cancelEnrollment(this)">취소</button>
                      </td>
                    </tr>
                  </c:forEach>
                </c:otherwise>
              </c:choose>
            </tbody>
          </table>
        </div>

        <!-- ✅ 뒤로가기 버튼 -->
        <div class="text-center mt-4">
          <button class="btn-back" onclick="location.href='/enrollments'">
            <i class="bi bi-arrow-left"></i> 수강 신청 목록으로 돌아가기
          </button>
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
  let totalCredits = ${currentCredits};
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
        totalCredits = data.currentCredits;
        document.getElementById("creditStatus").innerHTML =
          `<b>현재 신청 학점:</b> ${data.currentCredits}학점 <span class='text-gray-600'>(최대 ${data.maxCredits}학점)</span>`;
      } else if (res.status === 401) {
        alert("세션이 만료되었습니다. 다시 로그인해주세요.");
        window.location.href = "/auth/login";
      }
    } catch (err) {
      console.error("학점 갱신 오류:", err);
    }
  }

  // ✅ 수강 취소
  async function cancelEnrollment(button) {
    const row = button.closest("tr");
    const courseId = row.dataset.id;
    const token = localStorage.getItem("accessToken");

    if (!confirm("해당 강의를 수강 취소하시겠습니까?")) return;

    try {
      const response = await fetch("/api/enrollments/" + courseId, {
        method: "DELETE",
        headers: { "Authorization": `Bearer ${token}` }
      });

      if (!response.ok) {
        const errText = await response.text();
        alert("오류: " + errText);
        return;
      }

      await refreshCredits();
      alert("수강 신청이 취소되었습니다.");
      setTimeout(() => location.reload(), 300);
    } catch (e) {
      alert("네트워크 오류: " + e.message);
    }
  }
</script>
</body>
</html>
