<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>학기별 성적 조회</title>
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
        .student-info {
            background: white;
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .student-details {
            display: flex;
            gap: 40px;
        }
        .student-details div {
            text-align: left;
        }
        .student-details strong {
            color: #2c3e50;
            font-size: 1.1em;
        }
        .action-buttons {
            display: flex;
            gap: 10px;
        }
        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            font-size: 0.9em;
            transition: all 0.3s ease;
            text-align: center;
        }
        .btn-primary {
            background-color: #3498db;
            color: white;
        }
        .btn-primary:hover {
            background-color: #2980b9;
            transform: translateY(-1px);
        }
        .btn-secondary {
            background-color: #95a5a6;
            color: white;
        }
        .btn-secondary:hover {
            background-color: #7f8c8d;
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
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 15px;
            text-align: center;
            font-weight: 600;
        }
        .semester-table td {
            padding: 12px 15px;
            text-align: center;
            border-bottom: 1px solid #eee;
        }
        .semester-table tr:hover {
            background-color: #f8f9fa;
        }
        .semester-number {
            font-weight: bold;
            color: #2c3e50;
            font-size: 1.1em;
        }
        .total-score {
            color: #3498db;
            font-weight: bold;
            cursor: pointer;
        }
        .total-score:hover {
            text-decoration: underline;
        }
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
            border-bottom: 1px solid #eee;
            font-weight: bold;
            font-size: 1.1em;
        }
        .summary-table tr:last-child td {
            border-bottom: none;
            background-color: #f8f9fa;
        }
        .no-data {
            color: #95a5a6;
            font-style: italic;
        }
        .btn-sm {
            padding: 5px 10px;
            font-size: 0.8em;
        }
        .student-contact {
            display: flex;
            justify-content: space-between;
            margin-top: 10px;
            padding-top: 10px;
            border-top: 1px solid #eee;
        }
    </style>
</head>
<body>
<div class="container">
    <!-- 헤더 -->
    <div class="header">
        <h1>📊 성적 조회</h1>
        <div style="margin-top: 15px;">
            <a href="/" class="btn btn-outline-light">🏠 메인페이지</a>
        </div>
    </div>

    <!-- 학생 정보 -->
    <div class="student-info">
        <div class="student-details">
            <div>
                <strong>학번:</strong> ${studentInfo.studentNum != null ? studentInfo.studentNum : '-'}<br>
                <strong>학과:</strong> ${studentInfo.deptName != null ? studentInfo.deptName : '-'}
            </div>
            <div>
                <strong>성명:</strong> ${studentInfo.name != null ? studentInfo.name : '-'}<br>
                <strong>학생ID:</strong> ${studentId != null ? studentId : '-'}
            </div>
        </div>
        <div class="student-contact">
            <div>
                <strong>이메일:</strong> ${studentInfo.email != null ? studentInfo.email : '-'}<br>
                <strong>연락처:</strong> ${studentInfo.phone != null ? studentInfo.phone : '-'}
            </div>
        </div>
    </div>

    <!-- 학기별 성적 테이블 -->
    <table class="semester-table">
        <thead>
        <tr>
            <th>학기</th>
            <th>신청학점</th>
            <th>취득학점</th>
            <th>평균평점</th>
            <th>성적조회</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="semester" items="${result.data}">
            <tr>
                <td class="semester-number">${semester.semester}학기</td>
                <td>
                    <c:choose>
                        <c:when test="${semester.hasData}">
                            <fmt:formatNumber value="${semester.appliedCredits}" pattern="0.00"/>
                        </c:when>
                        <c:otherwise>
                            <span class="no-data">-</span>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${semester.hasData}">
                            <fmt:formatNumber value="${semester.acquiredCredits}" pattern="0.00"/>
                        </c:when>
                        <c:otherwise>
                            <span class="no-data">-</span>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${semester.hasData and semester.averageGpa > 0}">
                            <fmt:formatNumber value="${semester.averageGpa}" pattern="0.00"/>
                        </c:when>
                        <c:otherwise>
                            <span class="no-data">-</span>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${semester.hasData}">
                            <a href="${pageContext.request.contextPath}/grade/student/semester/${semester.semester}/detail" 
                               class="btn btn-primary btn-sm">조회</a>
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
            <th>신청학점</th>
            <th>이수학점</th>
            <th>평균평점</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td id="totalAppliedCredits">-</td>
            <td id="totalAcquiredCredits">-</td>
            <td id="overallGpa">-</td>
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
        let totalApplied = 0;
        let totalAcquired = 0;
        let totalGpa = 0;
        let validSemesters = 0;

        // 모든 학기 데이터를 순회하면서 합계 계산
        <c:forEach var="semester" items="${result.data}">
            <c:if test="${semester.hasData}">
                totalApplied += ${semester.appliedCredits};
                totalAcquired += ${semester.acquiredCredits};
                <c:if test="${semester.averageGpa > 0}">
                    totalGpa += ${semester.averageGpa};
                    validSemesters++;
                </c:if>
            </c:if>
        </c:forEach>

        // 평균 GPA 계산
        const avgGpa = validSemesters > 0 ? totalGpa / validSemesters : 0;

        // 결과 표시
        document.getElementById('totalAppliedCredits').textContent = totalApplied.toFixed(1);
        document.getElementById('totalAcquiredCredits').textContent = totalAcquired.toFixed(1);
        document.getElementById('overallGpa').textContent = avgGpa.toFixed(2);
    }
</script>
</body>
</html>


