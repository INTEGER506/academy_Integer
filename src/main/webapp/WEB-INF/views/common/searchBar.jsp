<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%-- 필수 파라미터 (include에서 넘겨야 함)
     - formAction         : 제출할 URL (절대/상대 경로)
     - optionValues       : 체크옵션 값 리스트 (예: "seq|title|date")
     - optionLabels       : 체크옵션 라벨 리스트 (예: "순번|제목|날짜")  -> optionValues와 개수/순서 동일
     - pageSizeOptions    : 페이지크기 리스트 (예: "5|10|15|20")
     - keep               : 모듈별 유지 hidden 덩어리 (예: studentId, deptId 등)
     - placeHolder        : 검색 input placeholder
     - req                : PageRequestDTO (req.page, req.pageSize, req.searchType, req.searchKeyword 사용)
--%>

<c:set var="vals"   value="${fn:split(param.optionValues, '|')}" />
<c:set var="labels" value="${fn:split(param.optionLabels, '|')}" />
<c:set var="sizes"  value="${fn:split(param.pageSizeOptions, '|')}" />


<form method="get" action="${formAction}">
  <!-- 검색 시 1페이지로 고정 -->
  <input type="hidden" name="page" value="1"/>

  <!-- 모듈별 유지 파라미터(필요 없으면 비워서 넘기기) -->
  <!-- 1. keepParams 방식 (우선) -->
  <c:if test="${not empty keepParams}">
    <c:forEach var="e" items="${keepParams}">
      <input type="hidden" name="${e.key}" value="${e.value}"/>
    </c:forEach>
  </c:if>

  <!-- 2. keep 문자열 방식 (하위 호환) -->
  <c:if test="${empty keepParams and not empty keep}">
    <c:forTokens var="param" items="${keep}" delims="&">
      <c:if test="${not empty param and fn:contains(param, '=')}">
        <c:set var="keyValue" value="${fn:split(param, '=')}"/>
        <c:if test="${fn:length(keyValue) == 2 and not empty keyValue[0] and not empty keyValue[1]}">
          <input type="hidden" name="${fn:trim(keyValue[0])}" value="${fn:trim(keyValue[1])}"/>
        </c:if>
      </c:if>
    </c:forTokens>
  </c:if>

  <div style="display:flex; gap:10px; align-items:center; flex-wrap:wrap; margin:6px 0;">
    <!-- 동적 체크 옵션 -->
    <select name="searchType" style="width:120px;">
      <c:forEach var="v" items="${vals}" varStatus="st">
        <option value="${fn:trim(v)}"
                <c:if test="${req.searchType == fn:trim(v)}">selected</c:if>>
            ${labels[st.index]}
        </option>
      </c:forEach>
    </select>

    <!-- 검색어 -->
    <input type="text" name="searchKeyword"
           value="${req.searchKeyword}" placeholder="${placeHolder}" style="width:220px;" />

    <button type="submit">검색</button>

    <!-- 페이지 사이즈: 옵션도 완전 커스텀 -->
    <select name="pageSize" onchange="this.form.submit()">
      <c:forEach var="sz" items="${sizes}">
        <option value="${sz}" <c:if test="${req.pageSize == sz}">selected</c:if>>${sz}개</option>
      </c:forEach>
    </select>
  </div>
</form>
