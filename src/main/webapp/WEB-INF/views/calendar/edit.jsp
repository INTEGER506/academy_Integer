<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>학사일정 수정</title>
  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
<div class="container">
  <h1>학사일정 수정</h1>

  <form id="editForm">
    <input type="hidden" id="id" name="id" value="${calendar.id}">

    <div>
      <label for="content">내용:</label>
      <input type="text" id="content" name="content" value="${calendar.content}" required>
    </div>
    <div>
      <label for="startDate">시작일:</label>
      <input type="date" id="startDate" name="startDate" value="${calendar.startDate}" required>
    </div>
    <div>
      <label for="endDate">종료일:</label>
      <input type="date" id="endDate" name="endDate" value="${calendar.endDate}">
    </div>
    <div>
      <label for="color">색상:</label>
      <input type="color" id="color" name="color" value="${calendar.color}">
    </div>

    <button type="submit">수정 완료</button>
    <a href="/calendar/list">취소</a>
  </form>
</div>

<script>
  $(document).ready(function() {
    $('#editForm').on('submit', function(e) {
      e.preventDefault(); // 폼 기본 제출 방지

      const id = $('#id').val();
      const formData = {
        id: id,
        content: $('#content').val(),
        startDate: $('#startDate').val(),
        endDate: $('#endDate').val(),
        color: $('#color').val() || null
      };

      // REST API PUT 요청
      $.ajax({
        url: `/api/calendar/${id}`,
        type: 'PUT', // HTTP PUT 메서드 사용
        contentType: 'application/json',
        data: JSON.stringify(formData),
        success: function(response) {
          alert(response); // 성공 메시지 ("일정이 성공적으로 수정되었습니다.")
          location.href = '/calendar/list'; // 목록 페이지로 이동
        },
        error: function(xhr) {
          let errorMessage = '일정 수정 중 오류가 발생했습니다.';
          if (xhr.status === 403) {
            errorMessage = '권한이 없습니다. 교직원/관리자만 일정을 수정할 수 있습니다.';
          } else if (xhr.responseJSON && xhr.responseJSON.message) {
            errorMessage = xhr.responseJSON.message;
          }
          alert(errorMessage);
        }
      });
    });
  });
</script>
</body>
</html>