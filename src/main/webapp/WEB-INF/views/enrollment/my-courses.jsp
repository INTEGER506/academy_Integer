<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>내 수강 목록</title>
  <style>
    body { font-family: "Pretendard", sans-serif; margin: 30px; }
    h1 { text-align: center; margin-bottom: 20px; }
    table { width: 100%; border-collapse: collapse; margin-top: 10px; }
    th, td { border: 1px solid #ccc; padding: 8px; text-align: center; font-size: 14px; }
    th { background-color: #f8f8f8; }
    .cancel-btn {
      background-color: #f44336; color: white; border: none;
      padding: 5px 10px; cursor: pointer; border-radius: 4px;
      transition: background-color 0.2s ease;
    }
    .cancel-btn:hover { background-color: #d32f2f; }

    .back-btn button {
      background-color: #444;
      color: white;
      border: none;
      padding: 6px 14px;
      border-radius: 4px;
      cursor: pointer;
    }
    .back-btn button:hover {
      background-color: #666;
    }
  </style>
</head>
<body>
<h1>내가 신청한 강의 목록</h1>

<!-- 현재 신청 학점 표시 -->
<p id="creditStatus"><b>현재 신청 학점:</b> ${currentCredits}학점 (최대 ${maxCredits}학점)</p>

<table id="courseTable">
  <thead>
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
      <tr><td colspan="7">현재 신청한 강의가 없습니다.</td></tr>
    </c:when>
    <c:otherwise>
      <c:forEach var="course" items="${myCourses}">
        <tr data-id="${course.id}" data-credit="${course.credit}">
          <td>${course.id}</td>
          <td>${course.subjectName}</td>
          <td>${course.professorName}</td>
          <td>${course.credit}</td>
          <td>${course.simpleDayTime}</td>
          <td>${course.simplePlace}</td>
          <td><button class="cancel-btn" onclick="cancelEnrollment(this)">취소</button></td>
        </tr>
      </c:forEach>
    </c:otherwise>
  </c:choose>
  </tbody>
</table>

<div class="back-btn" style="text-align:center; margin-top:20px;">
  <button onclick="location.href='/enrollments'">수강 신청 목록으로 돌아가기</button>
</div>

<script>
  let totalCredits = ${currentCredits};
  const maxCredits = ${maxCredits};

  // 서버 기준 최신 학점 동기화 함수
  async function refreshCredits() {
    try {
      const res = await fetch("/api/enrollments/credits");
      if (res.ok) {
        const data = await res.json();
        totalCredits = data.currentCredits;
        document.getElementById("totalCreditArea").innerHTML =
                `<b>현재 신청 학점:</b> ${data.currentCredits}학점 (최대 ${data.maxCredits}학점)`;
      }
    } catch (err) {
      console.error("학점 갱신 오류:", err);
    }
  }

  // 수강 취소 처리
  async function cancelEnrollment(button) {
    const row = button.closest("tr");
    const courseId = row.dataset.id;

    if (!confirm("해당 강의를 수강 취소하시겠습니까?")) return;

    try {
      const response = await fetch("/api/enrollments/" + courseId, { method: "DELETE" });
      if (!response.ok) throw new Error("수강 취소 중 오류가 발생했습니다.");

      // 서버 기준 학점 갱신
      await refreshCredits();

      // 알림 표시
      alert("수강 신청이 취소되었습니다.");
      setTimeout(() => location.reload(), 300);
    } catch (e) {
      alert(e.message);
    }
  }
</script>
</body>
</html>
