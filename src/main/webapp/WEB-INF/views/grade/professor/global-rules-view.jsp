<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>글로벌 규정 조회</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .container { max-width: 1000px; margin: 0 auto; }
        .btn { padding: 5px 10px; background-color: #f0f0f0; color: #333; border: 1px solid #ccc; cursor: pointer; text-decoration: none; display: inline-block; margin-right: 10px; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ccc; padding: 8px; text-align: center; }
        th { background-color: #f9f9f9; font-weight: bold; }
        .info-box { background-color: #f9f9f9; border: 1px solid #ddd; padding: 10px; margin-bottom: 20px; }
        .error-box { background-color: #f9f9f9; border: 1px solid #ddd; padding: 10px; margin-bottom: 20px; }
    </style>
</head>
<body>
    <div class="container">
        <h1>글로벌 성적 규정 조회</h1>
        
        <c:if test="${not empty error}">
            <div class="error-box">
                <strong>오류:</strong> ${error}
            </div>
        </c:if>
        
        <c:if test="${empty error}">
            <div class="info-box">
                <strong>정보:</strong> 이 페이지는 현재 적용 중인 글로벌 성적 규정을 조회하는 페이지입니다. (읽기 전용)
            </div>
            
            <c:choose>
                <c:when test="${not empty rules}">
                    <table>
                        <thead>
                            <tr>
                                <th>학점</th>
                                <th>최소점수</th>
                                <th>최대점수</th>
                                <th>GPA</th>
                                <th>설명</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="rule" items="${rules}">
                                <tr>
                                    <td><strong>${rule.gradeName}</strong></td>
                                    <td>${rule.minScore}점</td>
                                    <td>${rule.maxScore}점</td>
                                    <td>${rule.gpa}</td>
                                    <td>${rule.description}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <div style="text-align: center; padding: 40px;">
                        <p>글로벌 규정이 설정되지 않았습니다.</p>
                        <p>관리자에게 문의하시기 바랍니다.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </c:if>
        
        <!-- 네비게이션 -->
        <div style="margin-top: 30px; text-align: center;">
            <a href="${pageContext.request.contextPath}/grade/professor/courses?professorId=2" class="btn">내 강의 목록</a>
            <a href="${pageContext.request.contextPath}/test-index" class="btn btn-secondary">테스트 페이지로</a>
        </div>
    </div>
</body>
</html>
