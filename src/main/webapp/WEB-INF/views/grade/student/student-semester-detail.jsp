<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <title>학기별 상세 성적 조회</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            text-align: center;
        }
        .header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 30px;
            text-align: center;
        }
        .header h1 {
            margin: 0;
            font-size: 1.8em;
        }
        .back-button {
            display: inline-block;
            margin-bottom: 20px;
            padding: 10px 20px;
            background-color: #6c757d;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            transition: background-color 0.3s ease;
        }
        .back-button:hover {
            background-color: #5a6268;
            color: white;
            text-decoration: none;
        }
        .semester-table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 30px;
            background: white;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        .semester-table th {
            background: #f8f9fa;
            color: #495057;
            padding: 15px;
            text-align: center;
            font-weight: 600;
            border-bottom: 2px solid #dee2e6;
        }
        .semester-table td {
            padding: 12px 15px;
            text-align: center;
            border-bottom: 1px solid #dee2e6;
        }
        .semester-table tr:hover {
            background-color: #f8f9fa;
        }
        .subject-name {
            text-align: left;
            font-weight: 500;
        }
        .grade-letter {
            font-weight: bold;
            padding: 4px 8px;
            border-radius: 4px;
            color: black;
            display: inline-block;
            min-width: 20px;
            text-align: center;
            border: 1px solid #333;
        }
        .grade-a-plus { background-color: #28a745; }
        .grade-a { background-color: #20c997; }
        .grade-b-plus { background-color: #17a2b8; }
        .grade-b { background-color: #6f42c1; }
        .grade-c-plus { background-color: #fd7e14; }
        .grade-c { background-color: #dc3545; }
        .grade-d-plus { background-color: #6c757d; }
        .grade-d { background-color: #343a40; }
        .grade-f { background-color: #dc3545; }
        .summary-table {
            width: 100%;
            border-collapse: collapse;
            background: white;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        .summary-table th {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 15px;
            text-align: center;
            font-weight: 600;
        }
        .summary-table td {
            padding: 15px;
            text-align: center;
            border-bottom: 1px solid #dee2e6;
            font-weight: bold;
            font-size: 1.1em;
        }
        .summary-table tr:last-child td {
            border-bottom: none;
            background-color: #f8f9fa;
        }
        .no-data {
            color: #6c757d;
            font-style: italic;
        }
    </style>
</head>
<body>
<div class="container">
    <!-- 뒤로가기 버튼 -->
    <a href="${pageContext.request.contextPath}/grade/student/${studentId}/semester-grades" class="back-button">← 학기별 성적 조회로 돌아가기</a>

    <!-- 헤더 -->
    <div class="header">
        <h1>📚 ${semesterId}학기 상세 성적 조회</h1>
    </div>

    <!-- 학생 정보 (semester-grades.jsp와 비교용) -->
    <div class="student-info" style="background: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 20px; border: 1px solid #dee2e6;">
        <div style="display: flex; justify-content: space-between; margin-bottom: 10px;">
            <div>
                <strong>대학명:</strong> 창의공과대학<br>
                <strong>학번:</strong> ${studentInfo.studentNo != null ? studentInfo.studentNo : '-'}
            </div>
            <div>
                <strong>학과:</strong> ${studentInfo.major != null ? studentInfo.major : '-'}<br>
                <strong>성명:</strong> ${studentInfo.studentName != null ? studentInfo.studentName : '-'}
            </div>
        </div>
        <div style="display: flex; justify-content: space-between; padding-top: 10px; border-top: 1px solid #eee;">
            <div>
                <strong>이메일:</strong> ${studentInfo.email != null ? studentInfo.email : '-'}<br>
                <strong>연락처:</strong> ${studentInfo.phone != null ? studentInfo.phone : '-'}
            </div>
            <div>
                <strong>입학년도:</strong> ${studentInfo.admissionYear != null ? studentInfo.admissionYear : '-'}<br>
                <strong>학생ID:</strong> ${studentId != null ? studentId : '-'}
            </div>
        </div>
    </div>

    <!-- 학기별 상세 성적 테이블 -->
    <table class="semester-table">
        <thead>
        <tr>
            <th>교과목명</th>
            <th>학점</th>
            <th>평점</th>
            <th>점수</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="subject" items="${result.data}">
            <tr>
                <td class="subject-name">${subject.subjectName}</td>
                <td><fmt:formatNumber value="${subject.credit}" pattern="0.0"/></td>
                <td>
                    <c:choose>
                        <c:when test="${subject.gpa != null}">
                            <fmt:formatNumber value="${subject.gpa}" pattern="0.0"/>
                        </c:when>
                        <c:otherwise>
                            <span class="no-data">-</span>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${subject.alphabet != null && subject.alphabet != '' && subject.alphabet != '-'}">
                            <span class="grade-letter grade-${fn:toLowerCase(subject.alphabet)}">${subject.alphabet}</span>
                        </c:when>
                        <c:otherwise>
                            <span class="no-data">-</span>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <!-- 전체 요약 테이블 -->
    <table class="summary-table">
        <thead>
        <tr>
            <th>취득학점</th>
            <th>평균평점</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td id="totalCredits">-</td>
            <td id="averageGpa">-</td>
        </tr>
        </tbody>
    </table>
</div>

<script>
    // 페이지 로드 시 전체 요약 계산
    document.addEventListener('DOMContentLoaded', function() {
        calculateSummary();
    });

    function calculateSummary() {
        let totalCredits = 0;
        let totalGpa = 0;
        let validSubjects = 0;

        // 모든 과목 데이터를 순회하면서 합계 계산
        <c:forEach var="subject" items="${result.data}">
            totalCredits += ${subject.credit};
            <c:if test="${subject.gpa != null}">
                totalGpa += ${subject.gpa};
                validSubjects++;
            </c:if>
        </c:forEach>

        // 평균 GPA 계산
        const avgGpa = validSubjects > 0 ? totalGpa / validSubjects : 0;

        // 결과 표시
        document.getElementById('totalCredits').textContent = totalCredits.toFixed(1);
        document.getElementById('averageGpa').textContent = avgGpa.toFixed(2);
    }
</script>
</body>
</html>


