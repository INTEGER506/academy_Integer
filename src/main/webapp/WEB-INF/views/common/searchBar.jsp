<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%-- 검색 바 --%>
<form method="get" action="${formAction}">
    <input type="hidden" name="pageSize" value="${req.pageSize}">
    ${keep}

    <label>
        <input type="checkbox" name="searchType" value="n"
               <c:if test="${req.searchType != null && req.searchType.contains('n')}">checked</c:if>> 이름
    </label>
    <label>
        <input type="checkbox" name="searchType" value="m"
               <c:if test="${req.searchType != null && req.searchType.contains('m')}">checked</c:if>> 학번
    </label>
    <label>
        <input type="checkbox" name="searchType" value="s"
               <c:if test="${req.searchType != null && req.searchType.contains('s')}">checked</c:if>> 과목명
    </label>

    <input type="text" name="searchKeyword" value="${req.searchKeyword}" placeholder="${placeHolder}" />
    <button type="submit">검색</button>

    <select name="pageSize" onchange="this.form.submit()">
        <option value="5" <c:if test="${req.pageSize == 5}">selected</c:if>5개</option>
        <option value="10" <c:if test="${req.pageSize == 10}">selected</c:if>10개</option>
        <option value="15" <c:if test="${req.pageSize == 15}">selected</c:if>15개</option>
    </select>
</form>
