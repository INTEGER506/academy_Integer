<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>수강 신청</title>
    <style>
        body { font-family: sans-serif; margin: 20px; }
        table { width: 100%; border-collapse: collapse; margin-top: 10px; }
        th, td { border: 1px solid #ccc; padding: 6px; text-align: center; }
        th { background: #f2f2f2; }
        #message-area { text-align: center; font-weight: bold; margin-bottom: 15px; }
        button { padding: 4px 8px; cursor: pointer; }
        button[disabled] { background: #bbb; color: #fff; cursor: not-allowed; }
    </style>
</head>
<body>
<h1>수강 신청</h1>

<div id="message-area">
    <c:if test="${not empty message}"><p style="color:green;">${message}</p></c:if>
    <c:if test="${not empty error}"><p style="color:red;">${error}</p></c:if>
</div>

<p id="creditStatus"><b>현재 신청 학점:</b> ${currentCredits}학점 (최대 ${maxCredits}학점)</p>

<!-- 검색 -->
<div>
    <form action="/enrollments" method="get">
        <select name="searchType">
            <option value="all" ${param.searchType == 'all' ? 'selected' : ''}>전체</option>
            <option value="subject" ${param.searchType == 'subject' ? 'selected' : ''}>과목명</option>
            <option value="professor" ${param.searchType == 'professor' ? 'selected' : ''}>교수명</option>
            <option value="dept" ${param.searchType == 'dept' ? 'selected' : ''}>학과명</option>
        </select>
        <input type="text" name="searchKeyword" value="${param.searchKeyword}" placeholder="검색어 입력">
        <button type="submit">검색</button>
    </form>
</div>

<!-- 강의 목록 -->
<table>
    <thead>
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
            <td>${course.subjectName}</td>
            <td>${course.professorName}</td>
            <td>${course.credit}</td>
            <td>${course.simpleDayTime}</td>
            <td>${course.simplePlace}</td>
            <td class="num">${course.numOfStudent}</td>
            <td>${course.capacity}</td>
            <td>
                <button class="enroll-btn" ${course.isEnrolled ? 'disabled' : ''}>
                        ${course.isEnrolled ? '신청완료' : '신청'}
                </button>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<!-- 페이지네이션 -->
<div style="text-align:center; margin-top:15px;">
    <c:if test="${pageResponse != null}">
        <c:forEach var="i" begin="${pageResponse.startPage}" end="${pageResponse.endPage}">
            <c:choose>
                <c:when test="${i == pageResponse.currentPage}">
                    <strong>[${i}]</strong>
                </c:when>
                <c:otherwise>
                    <a href="?page=${i}&searchType=${param.searchType}&searchKeyword=${param.searchKeyword}">[${i}]</a>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </c:if>
</div>

<script>
    let currentCredits = ${currentCredits};
    const maxCredits = ${maxCredits};

    // ✅ 학점 갱신
    async function refreshCredits() {
        const token = localStorage.getItem("accessToken"); // JWT 가져오기
        try {
            const res = await fetch("/api/enrollments/credits", {
                headers: {
                    "Authorization": `Bearer ${token}` // ✅ 헤더에 토큰 포함
                }
            });
            if (res.ok) {
                const data = await res.json();
                currentCredits = data.currentCredits;
                document.getElementById("creditStatus").innerHTML =
                    `<b>현재 신청 학점:</b> ${currentCredits}학점 (최대 ${data.maxCredits}학점)`;
            } else if (res.status === 401) {
                alert("세션이 만료되었습니다. 다시 로그인해주세요.");
                window.location.href = "/auth/login";
            }
        } catch (err) {
            console.error("학점 갱신 오류:", err);
        }
    }

    // ✅ 수강 신청 버튼 클릭 이벤트
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
                        "Authorization": `Bearer ${token}`, // ✅ 헤더 추가
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
                e.target.style.background = "#bbb";

                let nowNum = parseInt(numCell.textContent) || 0;
                numCell.textContent = nowNum + 1;

                await refreshCredits();
                alert("수강 신청이 완료되었습니다.");
                setTimeout(() => location.reload(), 200);
            } catch (err) {
                alert("네트워크 오류: " + err.message);
            }
        });
    });
</script>
</body>
</html>
