<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%-- 공통 페이징 바 --%>

<div class="pagination">
    <c:set var="base" value="${baseUrl}"/>

    <%-- 이전 페이지 --%>
    <c:if test="${result.hasPrevious}">
        <a href="${base}?page=${result.startPage-1}&pageSize=${req.pageSize}${extraParams}">이전</a>
    </c:if>

    <%-- 페이지 번호 --%>
    <c:forEach var="p" begin="${result.startPage}" end="${result.endPage}">
        <c:choose>
            <c:when test="${p == result.currentPage}">
                <strong>[${p}]</strong>
            </c:when>
            <c:otherwise>
                <a href="${base}?page=${p}&pageSize=${req.pageSize}${extraParams}">[${p}]</a>
            </c:otherwise>
        </c:choose>
    </c:forEach>

    <!-- 다음 페이지 -->
    <c:if test="${result.hasNext}">
        <a href="${base}?page=${result.endPage + 1}&pageSize=${req.pageSize}${extraParams}">다음</a>
    </c:if>
</div>


