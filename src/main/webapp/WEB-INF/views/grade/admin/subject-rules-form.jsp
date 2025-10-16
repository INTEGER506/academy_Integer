<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>과목별 규정 설정</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .container { max-width: 800px; margin: 0 auto; }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; font-weight: bold; }
        input, select, textarea { width: 100%; padding: 5px; border: 1px solid #ccc; }
        .btn { padding: 5px 10px; background-color: #f0f0f0; color: #333; border: 1px solid #ccc; cursor: pointer; text-decoration: none; display: inline-block; margin-right: 10px; }
    </style>
</head>
<body>
    <div class="container">
        <h1>과목별 규정 설정</h1>
        
        <form action="${pageContext.request.contextPath}/grade/admin/subject-rules/save" method="post">
            <div class="form-group">
                <label for="subjectId">과목 선택:</label>
                <select id="subjectId" name="subjectId" required>
                    <option value="">과목을 선택하세요</option>
                    <option value="1">알고리즘</option>
                    <option value="2">데이터베이스</option>
                    <option value="3">웹프로그래밍</option>
                </select>
            </div>
            
            <div class="form-group">
                <label for="gradeName">학점명:</label>
                <input type="text" id="gradeName" name="gradeName" placeholder="예: A+, A, B+, B, C+, C, D+, D, F" required>
            </div>
            
            <div class="form-group">
                <label for="minScore">최소 점수:</label>
                <input type="number" id="minScore" name="minScore" min="0" max="100" placeholder="0" required>
            </div>
            
            <div class="form-group">
                <label for="maxScore">최대 점수:</label>
                <input type="number" id="maxScore" name="maxScore" min="0" max="100" placeholder="100" required>
            </div>
            
            <div class="form-group">
                <label for="gpa">GPA:</label>
                <input type="number" id="gpa" name="gpa" step="0.1" min="0" max="4.5" placeholder="4.5" required>
            </div>
            
            <div class="form-group">
                <label for="description">설명:</label>
                <textarea id="description" name="description" rows="3" placeholder="규정에 대한 추가 설명"></textarea>
            </div>
            
            <div class="form-group">
                <button type="submit" class="btn">저장</button>
                <a href="${pageContext.request.contextPath}/grade/admin/subject-rules/list" class="btn btn-secondary">목록으로</a>
            </div>
        </form>
    </div>
</body>
</html>
