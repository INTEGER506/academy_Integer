<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>점수 분배 비율 설정</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 1000px;
            margin: 0 auto;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            text-align: center;
        }
        .header h1 {
            margin: 0;
            font-size: 28px;
            font-weight: 300;
        }
        .header p {
            margin: 10px 0 0 0;
            opacity: 0.9;
            font-size: 14px;
        }
        .content {
            padding: 30px;
        }
        .search-section {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 25px;
        }
        .table-container {
            overflow-x: auto;
            border-radius: 8px;
            border: 1px solid #dee2e6;
        }
        .table {
            width: 100%;
            border-collapse: collapse;
            margin: 0;
            background: white;
        }
        .table thead {
            background: linear-gradient(135deg, #6c757d 0%, #495057 100%);
            color: white;
        }
        .table th {
            padding: 15px 12px;
            text-align: center;
            font-weight: 500;
            border: none;
        }
        .table tbody tr {
            border-bottom: 1px solid #dee2e6;
            transition: background-color 0.2s ease;
        }
        .table tbody tr:hover {
            background-color: #f8f9fa;
        }
        .table tbody tr:nth-child(even) {
            background-color: #f8f9fa;
        }
        .table tbody tr:nth-child(even):hover {
            background-color: #e9ecef;
        }
        .table td {
            padding: 12px;
            text-align: center;
            border: none;
        }
        .table td:first-child {
            font-weight: 500;
            color: #6c757d;
        }
        .table td:nth-child(2) {
            text-align: left;
            font-weight: 500;
            color: #495057;
        }
        .percentage {
            font-weight: 500;
            color: #28a745;
        }
        .btn-edit {
            background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
            color: white;
            padding: 8px 16px;
            text-decoration: none;
            border-radius: 6px;
            font-size: 13px;
            font-weight: 500;
            transition: all 0.2s ease;
            display: inline-block;
            border: none;
            cursor: pointer;
        }
        .btn-edit:hover {
            transform: translateY(-1px);
            box-shadow: 0 4px 8px rgba(0,123,255,0.3);
            color: white;
            text-decoration: none;
        }
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #6c757d;
        }
        .empty-state i {
            font-size: 48px;
            margin-bottom: 20px;
            opacity: 0.5;
        }
        .pagination-section {
            margin-top: 30px;
            padding: 20px;
            background-color: #f8f9fa;
            border-radius: 8px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>📊 점수 분배 비율 설정</h1>
            <p>각 강의별로 중간고사, 기말고사, 과제, 출석 점수의 비율을 설정할 수 있습니다</p>
        </div>
        
        <div class="content">
            <!-- 검색 섹션 -->
            <div class="search-section">
                <jsp:include page="/WEB-INF/views/common/searchBar.jsp">
                    <jsp:param name="formAction"      value="${pageContext.request.contextPath}/grade/professor/system-list"/>
                    <jsp:param name="optionValues"    value="courseName"/>
                    <jsp:param name="optionLabels"    value="강의명"/>
                    <jsp:param name="pageSizeOptions" value="10|20|50"/>
                    <jsp:param name="placeHolder"     value="강의명 검색"/>
                    <jsp:param name="keep"            value="professorId=${professorId}"/>
                    <jsp:param name="req"             value="${req}"/>
                </jsp:include>
            </div>

            <!-- 테이블 섹션 -->
            <div class="table-container">
                <table class="table">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>강의명</th>
                            <th>중간고사</th>
                            <th>기말고사</th>
                            <th>과제</th>
                            <th>출석</th>
                            <th>총합</th>
                            <th>관리</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="course" items="${result.data}" varStatus="st">
                            <tr>
                                <td>${(result.currentPage - 1) * result.pageSize + st.index + 1}</td>
                                <td>${course.SUBJECTNAME}</td>
                                <td><span class="percentage">${course.gradeSystem != null && course.gradeSystem.midExamRatio != null ? course.gradeSystem.midExamRatio.intValue() : 30}%</span></td>
                                <td><span class="percentage">${course.gradeSystem != null && course.gradeSystem.finalExamRatio != null ? course.gradeSystem.finalExamRatio.intValue() : 40}%</span></td>
                                <td><span class="percentage">${course.gradeSystem != null && course.gradeSystem.assignmentRatio != null ? course.gradeSystem.assignmentRatio.intValue() : 20}%</span></td>
                                <td><span class="percentage">${course.gradeSystem != null && course.gradeSystem.attendanceRatio != null ? course.gradeSystem.attendanceRatio.intValue() : 10}%</span></td>
                                <td><span class="percentage">100%</span></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/grade/professor/system/edit?professorId=${professorId}&courseId=${course.COURSEID}&subjectId=${course.SUBJECTID}${course.gradeSystem != null && course.gradeSystem.id != null ? '&id=' : ''}${course.gradeSystem != null ? course.gradeSystem.id : ''}" 
                                       class="btn-edit">✏️ 수정</a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty result.data}">
                            <tr>
                                <td colspan="8">
                                    <div class="empty-state">
                                        <div style="font-size: 48px; margin-bottom: 20px; opacity: 0.5;">📚</div>
                                        <h3>등록된 강의가 없습니다</h3>
                                        <p>강의 관리에서 먼저 강의를 등록해주세요.</p>
                                    </div>
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <!-- 페이지네이션 섹션 -->
            <div class="pagination-section">
                <jsp:include page="/WEB-INF/views/common/page.jsp">
                    <jsp:param name="baseUrl"    value="${pageContext.request.contextPath}/grade/professor/system-list"/>
                    <jsp:param name="req"        value="${req}"/>
                    <jsp:param name="result"     value="${result}"/>
                    <jsp:param name="keepParams" value="${keepParams}"/>
                </jsp:include>
            </div>
        </div>
    </div>
</body>
</html>