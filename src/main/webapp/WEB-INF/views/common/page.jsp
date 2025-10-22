<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<form method="get" action="${baseUrl}">
  <!-- 공통 유지 -->
  <input type="hidden" name="pageSize" value="${req.pageSize}"/>

  <!-- 모듈별 유지 파라미터 자동 주입 -->
  <c:if test="${not empty keepParams}">
    <c:forEach var="e" items="${keepParams}">
      <input type="hidden" name="${e.key}" value="${e.value}"/>
    </c:forEach>
  </c:if>

  <div class="pagination" style="display:flex; gap:6px; align-items:center; flex-wrap:wrap;">

    <!-- 이전 블록 -->
    <c:if test="${result.hasPrevious}">
      <button type="submit" name="page" value="${result.startPage - 1}">이전</button>
    </c:if>

    <!-- 현재 블록 페이지들 -->
    <c:forEach var="p" begin="${result.startPage}" end="${result.endPage}">
      <button type="submit" name="page" value="${p}"
              <c:if test="${p == result.currentPage}">disabled</c:if>>
        [${p}]
      </button>
    </c:forEach>

    <!-- 다음 블록 -->
    <c:if test="${result.hasNext}">
      <button type="submit" name="page" value="${result.endPage + 1}">다음</button>
    </c:if>

    <!-- (선택) 전체/현재 페이지 표시 -->
    <span style="margin-left:8px;">
      ${result.currentPage} / ${result.totalPage}
    </span>
  </div>
</form>