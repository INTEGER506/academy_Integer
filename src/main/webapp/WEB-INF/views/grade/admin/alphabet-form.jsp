<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>점수 규정 관리</title>
</head>
<body>

<h2>점수 규정 ${mode eq 'edit' ? '수정' : '등록'}</h2>

<c:set var="actionUrl"
       value="${pageContext.request.contextPath}/grade/admin/rule/${mode eq 'edit' ? 'edit' : 'global/add'}"/>
<form method="post" action="${actionUrl}">
    <c:if test="${mode eq 'edit'}">
        <input type="hidden" name="id" value="${alphabet.id}" />
    </c:if>

    <table border="1">
        <tr>
            <th>등급</th>
            <td><input type="text" name="alphabet" value="${alphabet.alphabet}" required /></td>
        </tr>
        <tr>
            <th>최소 점수</th>
            <td><input type="number" name="minScore" value="${alphabet.minScore}" required /></td>
        </tr>
        <tr>
            <th>최대 점수</th>
            <td><input type="number" name="maxScore" value="${alphabet.maxScore}" required /></td>
        </tr>
        <tr>
            <th>비율(%)</th>
            <td><input type="number" name="ratio" value="${alphabet.ratio}"  min="0" max="100" /></td>
        </tr>
    </table>

    <div style="margin-top: 12px;">
        <button type="submit">${mode eq 'edit' ? '저장' : '등록'}</button>
        <a href="${pageContext.request.contextPath}/grade/admin/rule/global">목록</a>
    </div>
</form>
</body>
</html>
