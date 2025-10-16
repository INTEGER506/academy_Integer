<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>과목별 규정 조회</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .container { max-width: 1000px; margin: 0 auto; }
        .btn { padding: 5px 10px; background-color: #f0f0f0; color: #333; border: 1px solid #ccc; cursor: pointer; text-decoration: none; display: inline-block; margin-right: 10px; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ccc; padding: 8px; text-align: center; }
        th { background-color: #f9f9f9; font-weight: bold; }
        .info-box { background-color: #f9f9f9; border: 1px solid #ddd; padding: 10px; margin-bottom: 20px; }
        .error-box { background-color: #f9f9f9; border: 1px solid #ddd; padding: 10px; margin-bottom: 20px; }
        .subject-info { background-color: #f9f9f9; border: 1px solid #ddd; padding: 10px; margin-bottom: 20px; }
    </style>
</head>
<body>
    <div class="container">
        <h1>과목별 성적 규정 조회</h1>
        
        <c:if test="${not empty error}">
            <div class="error-box">
                <strong>오류:</strong> ${error}
            </div>
        </c:if>
        
        <c:if test="${empty error}">
            <div class="subject-info">
                <strong>과목 ID:</strong> ${subjectId}<br>
                <strong>과목명:</strong> 
                <c:choose>
                    <c:when test="${subjectId == 1}">알고리즘</c:when>
                    <c:when test="${subjectId == 2}">데이터베이스</c:when>
                    <c:when test="${subjectId == 3}">웹프로그래밍</c:when>
                    <c:otherwise>기타 과목</c:otherwise>
                </c:choose>
            </div>
            
            <div class="info-box">
                <strong>정보:</strong> 이 페이지는 선택된 과목의 성적 규정을 조회하는 페이지입니다. (읽기 전용)
            </div>
            
            <c:if test="${not empty message}">
                <div class="info-box">
                    <strong>메시지:</strong> ${message}
                </div>
            </c:if>
            
            <!-- 과목별 규정 테이블 -->
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
                    <tr>
                        <td colspan="5" style="text-align: center; padding: 40px;">
                            <p>이 과목에 대한 특별한 규정이 설정되지 않았습니다.</p>
                            <p>글로벌 규정이 적용됩니다.</p>
                        </td>
                    </tr>
                </tbody>
            </table>
            
            <!-- 글로벌 규정 적용 안내 -->
            <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; color: #856404; padding: 15px; border-radius: 5px; margin-top: 20px;">
                <strong>안내:</strong> 과목별 특별 규정이 없는 경우, 시스템의 글로벌 규정이 적용됩니다.
            </div>
        </c:if>
        
        <!-- 네비게이션 -->
        <div style="margin-top: 30px; text-align: center;">
            <a href="${pageContext.request.contextPath}/grade/professor/rule/global" class="btn">글로벌 규정 조회</a>
            <a href="${pageContext.request.contextPath}/grade/professor/courses?professorId=2" class="btn">내 강의 목록</a>
            <a href="${pageContext.request.contextPath}/test-index" class="btn btn-secondary">테스트 페이지로</a>
        </div>
    </div>
</body>
</html>
