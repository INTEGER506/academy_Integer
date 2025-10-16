<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>과목별 규정 목록</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .container { max-width: 1200px; margin: 0 auto; }
        .btn { padding: 5px 10px; background-color: #f0f0f0; color: #333; border: 1px solid #ccc; cursor: pointer; text-decoration: none; display: inline-block; margin-right: 5px; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
        th { background-color: #f9f9f9; font-weight: bold; }
        .search-form { margin-bottom: 20px; padding: 10px; border: 1px solid #ccc; }
        .search-form input, .search-form select { padding: 5px; margin-right: 10px; border: 1px solid #ccc; }
    </style>
</head>
<body>
    <div class="container">
        <h1>과목별 규정 목록</h1>
        
        <!-- 검색 폼 -->
        <div class="search-form">
            <form method="get" action="${pageContext.request.contextPath}/grade/admin/subject-rules/list">
                <select name="searchType">
                    <option value="">검색 조건</option>
                    <option value="subject">과목명</option>
                    <option value="grade">학점</option>
                </select>
                <input type="text" name="searchKeyword" placeholder="검색어를 입력하세요" value="${param.searchKeyword}">
                <button type="submit" class="btn">검색</button>
                <a href="${pageContext.request.contextPath}/grade/admin/subject-rules/form" class="btn btn-success">규정 추가</a>
            </form>
        </div>
        
        <!-- 규정 목록 테이블 -->
        <table>
            <thead>
                <tr>
                    <th>번호</th>
                    <th>과목명</th>
                    <th>학점</th>
                    <th>최소점수</th>
                    <th>최대점수</th>
                    <th>GPA</th>
                    <th>설명</th>
                    <th>작업</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td colspan="8" style="text-align: center; padding: 40px;">
                        <p>과목별 규정이 등록되지 않았습니다.</p>
                        <p>새로운 규정을 추가해보세요!</p>
                        <a href="${pageContext.request.contextPath}/grade/admin/subject-rules/form" class="btn btn-success">규정 추가하기</a>
                    </td>
                </tr>
            </tbody>
        </table>
        
        <!-- 페이지네이션 -->
        <div style="text-align: center; margin-top: 20px;">
            <span>페이지 1 / 1</span>
        </div>
        
        <!-- 네비게이션 -->
        <div style="margin-top: 20px;">
            <a href="${pageContext.request.contextPath}/grade/admin/rule/global" class="btn">글로벌 규정 목록</a>
            <a href="${pageContext.request.contextPath}/test-index" class="btn btn-secondary">테스트 페이지로</a>
        </div>
    </div>
</body>
</html>
