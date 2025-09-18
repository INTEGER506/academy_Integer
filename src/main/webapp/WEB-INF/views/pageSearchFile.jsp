<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Title</title>
</head>
<body>
<%-- 검색 폼 (키워드 검색)--%>
<form action="" method="get">
    <select name="searchType">
        <option value="title">제목</option>
        <option value="content">내용</option>
    </select>
    <input type="text" name="searchKeyword" placeholder="검색어를 입력하세요.">
    <button type="submit">검색</button>
</form>

<%-- 페이지 번호 링크 --%>
<div class="pagination">
    <c:forEach begin="1" end="${pageResponseDTO.totalPage}" var="page">
        <a href="/notice?page=${page}">${page}</a>
    </c:forEach>
</div>

<%-- 파일 업로드 --%>
<form action="/upload" method="post" enctype="multipart/form-data">
    <input type="file" name="file">
    <button type="submit">업로드</button>
</form>
</body>
</html>
