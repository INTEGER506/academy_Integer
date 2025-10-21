<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>📚 내가 개설한 강의 목록</h2>
<p>강의를 선택하여 해당 강의의 수강생 목록을 확인하고 성적을 관리할 수 있습니다.</p>

<!-- 디버그 정보 -->
<div style="background-color: #f0f0f0; padding: 10px; margin: 10px 0; border: 1px solid #ccc;">
    <h4>🔍 디버그 정보</h4>
    <p><strong>professorId:</strong> ${professorId}</p>
    <p><strong>result.data size:</strong> ${result.data.size()}</p>
    <p><strong>result.totalCount:</strong> ${result.totalCount}</p>
    <c:if test="${not empty result.data}">
        <p><strong>첫 번째 강의:</strong> ${result.data[0]}</p>
    </c:if>
</div>

<%-- =====================[ 검색 바 공용 include ]===================== --%>
<jsp:include page="/WEB-INF/views/common/searchBar.jsp">
    <jsp:param name="formAction"      value="${pageContext.request.contextPath}/grade/professor/courses"/>
    <jsp:param name="optionValues"    value="courseName|subjectName"/>
    <jsp:param name="optionLabels"    value="강의명|과목명"/>
    <jsp:param name="pageSizeOptions" value="10|20|50"/>
    <jsp:param name="placeHolder"     value="강의명 또는 과목명 입력"/>
    <jsp:param name="keep"            value="professorId=${professorId}"/>
    <jsp:param name="req"             value="${req}"/>
</jsp:include>

<table border="1" style="border-collapse: collapse; width: 100%; margin-top: 20px;">
    <thead>
    <tr style="background-color: #007bff; color: white;">
        <th style="padding: 10px;">#</th>
        <th style="padding: 10px;">강의명</th>
        <th style="padding: 10px;">과목명</th>
        <th style="padding: 10px;">학점</th>
        <th style="padding: 10px;">학기</th>
        <th style="padding: 10px;">수강생 수</th>
        <th style="padding: 10px;">관리</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="course" items="${result.data}" varStatus="st">
        <tr style="background-color: #f8f9fa;">
            <td style="padding: 10px; text-align: center;">${(result.currentPage - 1) * result.pageSize + st.index + 1}</td>
            <td style="padding: 10px;">
                <strong style="color: #007bff;">📖 ${course.COURSENAME}</strong>
            </td>
            <td style="padding: 10px;">${course.SUBJECTNAME}</td>
            <td style="padding: 10px; text-align: center;">${course.CREDIT}학점</td>
            <td style="padding: 10px;">${course.SEMESTERNAME}</td>
            <td style="padding: 10px; text-align: center;">
                <span style="background-color: #e3f2fd; padding: 4px 8px; border-radius: 4px; font-weight: bold;">
                    ${course.ENROLLMENTCOUNT}명
                </span>
            </td>
            <td style="padding: 10px; text-align: center;">
                <a href="${pageContext.request.contextPath}/grade/professor/list?professorId=${professorId}&courseId=${course.COURSEID}&subjectId=${course.SUBJECTID}" 
                   style="background-color: #28a745; color: white; padding: 8px 16px; text-decoration: none; border-radius: 4px; font-weight: bold;">
                    👥 수강생 관리
                </a>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty result.data}">
        <tr>
            <td colspan="7" style="padding: 20px; text-align: center; color: #6c757d;">
                📝 개설한 강의가 없습니다.
            </td>
        </tr>
    </c:if>
    </tbody>
</table>

<%-- =====================[ 페이징 바 공용 include ]===================== --%>
<jsp:include page="/WEB-INF/views/common/page.jsp">
    <jsp:param name="baseUrl" value="${pageContext.request.contextPath}/grade/professor/courses"/>
    <jsp:param name="req"     value="${req}"/>
    <jsp:param name="result"  value="${result}"/>
    <jsp:param name="keepParams" value="${keepParams}"/>
</jsp:include>

<div style="margin-top: 20px; padding: 15px; background-color: #f8f9fa; border-radius: 8px;">
    <h4 style="color: #495057; margin-bottom: 10px;">💡 사용 방법</h4>
    <ol style="color: #6c757d; margin: 0;">
        <li><strong>강의 선택:</strong> "👥 수강생 관리" 버튼을 클릭하여 해당 강의의 수강생 목록을 확인합니다.</li>
        <li><strong>성적 관리:</strong> 수강생 목록에서 개별 학생의 성적을 등록, 수정, 삭제할 수 있습니다.</li>
        <li><strong>점수 분배:</strong> 강의별로 중간고사, 기말고사, 과제, 출석 비율을 설정할 수 있습니다.</li>
    </ol>
</div>


