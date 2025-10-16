<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>글로벌 성적 규정</h2>
<p>학교 전체에 적용되는 기본 성적 분배 비율입니다.</p>

<div style="max-width: 400px; margin: 20px auto;">
    <table style="width: 100%; border-collapse: collapse; border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
        <thead>
        <tr style="background-color: #007bff; color: white;">
            <th style="padding: 15px; text-align: center; border-bottom: 1px solid #dee2e6;">학점</th>
            <th style="padding: 15px; text-align: center; border-bottom: 1px solid #dee2e6;">비율 (%)</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="row" items="${result.data}" varStatus="st">
            <tr style="background-color: ${st.index % 2 == 0 ? '#ffffff' : '#f8f9fa'};">
                <td style="padding: 12px; text-align: center; border-bottom: 1px solid #dee2e6; font-weight: bold; font-size: 16px;">${row.alphabet}</td>
                <td style="padding: 12px; text-align: center; border-bottom: 1px solid #dee2e6; font-size: 16px;">${row.boundary}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty result.data}">
            <tr><td colspan="2" style="padding: 30px; text-align: center; color: #6c757d; font-style: italic;">등록된 규정이 없습니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>

<div style="text-align: center; margin-top: 30px;">
    <a href="${pageContext.request.contextPath}/grade/admin/global-rules/form" 
       style="display: inline-block; background-color: #007bff; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; margin: 0 10px; font-weight: bold; box-shadow: 0 2px 4px rgba(0,123,255,0.3); transition: background-color 0.3s;"
       onmouseover="this.style.backgroundColor='#0056b3'" 
       onmouseout="this.style.backgroundColor='#007bff'">
        ✏️ 전체 규정 설정
    </a>
    <a href="${pageContext.request.contextPath}/grade/admin/subject-rules/list" 
       style="display: inline-block; background-color: #28a745; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; margin: 0 10px; font-weight: bold; box-shadow: 0 2px 4px rgba(40,167,69,0.3); transition: background-color 0.3s;"
       onmouseover="this.style.backgroundColor='#1e7e34'" 
       onmouseout="this.style.backgroundColor='#28a745'">
        📚 과목별 규정 관리
    </a>
</div>