<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>학사일정 수정 | 학사정보관리시스템</title>

  <!-- 폰트 / Bootstrap -->
  <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;700&family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>
  <link rel="stylesheet" href="<c:url value='/css/style.css'/>"/>
  <link rel="icon" href="data:,">
</head>
<body class="bg-page">

<%@ include file="/WEB-INF/views/components/header.jsp" %>

<main class="py-4">
  <div class="container-1200 d-flex gap-24">
    <%@ include file="/WEB-INF/views/components/sidebar.jsp" %>

    <section class="flex-1 d-flex flex-column gap-24">

      <!-- 페이지 타이틀 -->
      <div class="d-flex justify-content-between align-items-center mb-3">
        <h4 class="fw-bold mb-0">학사일정 수정</h4>
        <a href="<c:url value='/calendar/list'/>" class="btn btn-outline-secondary rounded-pill">
          <i class="bi bi-arrow-left"></i> 목록으로
        </a>
      </div>

      <!-- 수정 폼 -->
      <div class="card-white p-4">
        <form id="editForm" class="d-flex flex-column gap-3">
          <input type="hidden" id="id" name="id" value="${calendar.id}">

          <div class="row g-3">
            <div class="col-12 col-md-6">
              <label for="content" class="form-label fw-semibold">내용</label>
              <input type="text" id="content" name="content"
                     class="form-control"
                     value="${calendar.content}"
                     placeholder="예: 2학기 개강"
                     required>
            </div>
            <div class="col-12 col-md-3">
              <label for="startDate" class="form-label fw-semibold">시작일</label>
              <input type="date" id="startDate" name="startDate"
                     class="form-control"
                     value="${calendar.startDate}"
                     required>
            </div>
            <div class="col-12 col-md-3">
              <label for="endDate" class="form-label fw-semibold">종료일</label>
              <input type="date" id="endDate" name="endDate"
                     class="form-control"
                     value="${calendar.endDate}">
            </div>
            <div class="col-12 col-md-3">
              <label for="color" class="form-label fw-semibold">색상</label>
              <input type="color" id="color" name="color"
                     class="form-control form-control-color"
                     value="${calendar.color != null ? calendar.color : '#0d6efd'}">
            </div>
          </div>

          <div class="mt-4 d-flex gap-2 justify-content-end">
            <a href="<c:url value='/calendar/list'/>" class="btn btn-light rounded-pill px-4">취소</a>
            <button type="submit" class="btn btn-navy rounded-pill px-4">수정 완료</button>
          </div>
        </form>
      </div>
    </section>
  </div>
</main>

<%@ include file="/WEB-INF/views/components/footer.jsp" %>

<!-- 스크립트 -->
<script src="<c:url value='/vendor/jquery/jquery-3.7.1.min.js'/>"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
  $(document).ready(function() {
    $('#editForm').on('submit', function(e) {
      e.preventDefault();

      const id = $('#id').val();
      const formData = {
        id: id,
        content: $('#content').val(),
        startDate: $('#startDate').val(),
        endDate: $('#endDate').val(),
        color: $('#color').val() || null
      };

      $.ajax({
        url: `/api/calendar/${id}`,
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(formData),
        success: function(response) {
          alert(response || '일정이 성공적으로 수정되었습니다.');
          location.href = '/calendar/list';
        },
        error: function(xhr) {
          let msg = '일정 수정 중 오류가 발생했습니다.';
          if (xhr.status === 403) {
            msg = '권한이 없습니다. 교직원/관리자만 수정할 수 있습니다.';
          } else if (xhr.responseJSON && xhr.responseJSON.message) {
            msg = xhr.responseJSON.message;
          }
          alert(msg);
        }
      });
    });
  });
</script>

</body>
</html>
