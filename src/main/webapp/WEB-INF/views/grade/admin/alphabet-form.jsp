<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>${empty as.id ? "글로벌 규정 추가" : "글로벌 규정 수정"}</h2>

<form method="post" action="${action}">
    <input type="hidden" name="id" value="${as.id}"/>

    <div>
        <label>학점</label>
        <select name="alphabet" required>
            <c:set var="opts" value="A+|A|B+|B|C+|C|D+|D|F"/>
            <c:forTokens var="opt" items="${opts}" delims="|">
                <option value="${opt}" <c:if test="${as.alphabet == opt}">selected</c:if>>${opt}</option>
            </c:forTokens>
        </select>
    </div>

    <div>
        <label>경계값</label>
        <input type="number" step="0.1" min="0" max="100" name="boundary" value="${as.boundary}" required/>
        <small>예) 95 → 95점 이상이면 해당 학점</small>
    </div>

    <div class="mt-2">
        <button type="submit">저장</button>
        <a href="${pageContext.request.contextPath}/grade/admin/alphabet-list">목록</a>
    </div>
</form>