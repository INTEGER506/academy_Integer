<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>성적 관리 시스템 테스트</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
        }
        .container {
            max-width: 1000px;
            margin: 0 auto;
        }
        .section {
            margin-bottom: 30px;
            padding: 15px;
            border: 1px solid #ccc;
        }
        .btn-group {
            margin-top: 10px;
        }
        .btn {
            display: inline-block;
            padding: 8px 16px;
            margin: 5px;
            background-color: #f0f0f0;
            color: #333;
            text-decoration: none;
            border: 1px solid #ccc;
        }
        .note {
            background-color: #f9f9f9;
            border: 1px solid #ddd;
            padding: 10px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>성적 관리 시스템 테스트 페이지</h1>

        <!-- 관리자 기능 -->
        <div class="section">
            <h2>관리자 기능</h2>
            <p>성적 규정 관리 및 전체 시스템 설정</p>
            <div class="btn-group">
                <a href="${pageContext.request.contextPath}/grade/admin/rule/global" class="btn btn-primary">글로벌 성적 규정 목록</a>
                <a href="${pageContext.request.contextPath}/grade/admin/global-rules/form" class="btn btn-success">글로벌 규정 설정</a>
                <a href="${pageContext.request.contextPath}/grade/admin/subject-rules/form" class="btn btn-info">과목별 규정 설정</a>
                <a href="${pageContext.request.contextPath}/grade/admin/subject-rules/list" class="btn btn-warning">과목별 규정 목록</a>
            </div>
        </div>

        <!-- 교수 기능 -->
        <div class="section">
            <h2>교수 기능</h2>
            <p>강의 관리, 성적 등록, 수정 및 규정 조회</p>
            <div class="btn-group">
                <a href="${pageContext.request.contextPath}/grade/professor/courses?professorId=2" class="btn btn-primary">내가 개설한 강의 목록</a>
                <a href="${pageContext.request.contextPath}/grade/professor/list?professorId=2&courseId=1&subjectId=1" class="btn btn-success">강의별 성적 관리 (인라인 등록)</a>
                <a href="${pageContext.request.contextPath}/grade/professor/system-list?courseId=1" class="btn btn-info">점수 분배 비율 설정</a>
                <a href="${pageContext.request.contextPath}/grade/professor/rule/global" class="btn btn-secondary">글로벌 규정 조회</a>
                <a href="${pageContext.request.contextPath}/grade/professor/rule/subject/1" class="btn btn-secondary">과목별 규정 조회</a>
            </div>
        </div>

        <!-- 학생 기능 -->
        <div class="section">
            <h2>학생 기능</h2>
            <p>개인 성적 조회 및 GPA 확인</p>
            <div class="btn-group">
                <a href="${pageContext.request.contextPath}/grade/student/3/list" class="btn btn-primary">내 성적 목록</a>
                <a href="${pageContext.request.contextPath}/grade/student/3/detail/1" class="btn btn-success">성적 상세보기</a>
            </div>
        </div>

        <!-- 테스트 데이터 -->
        <div class="section">
            <h2>테스트 데이터</h2>
            <p>테스트에 사용할 수 있는 기본 데이터</p>
            <ul>
                <li><strong>courseId:</strong> 1 (알고리즘 강의)</li>
                <li><strong>subjectId:</strong> 1 (알고리즘 과목)</li>
                <li><strong>studentId:</strong> 3 (김학생)</li>
                <li><strong>professorId:</strong> 2 (김교수)</li>
                <li><strong>enrollmentId:</strong> 1</li>
            </ul>
        </div>

        <!-- 직접 URL 테스트 -->
        <div class="section">
            <h2>직접 URL 테스트</h2>
            <p>브라우저 주소창에 직접 입력하여 테스트</p>
            <div style="background-color: #f8f9fa; padding: 15px; border-radius: 5px; font-family: monospace;">
                <div>• 관리자 글로벌 규정: <code>/grade/admin/global-rules/form</code></div>
                <div>• 교수 점수 비율 설정: <code>/grade/professor/system/edit?courseId=1&subjectId=1</code></div>
                <div>• 학생 성적 목록: <code>/grade/student/3/list</code></div>
                <div>• 교수 강의 목록: <code>/grade/professor/courses?professorId=2</code></div>
            </div>
        </div>
    </div>
</body>
</html>
